package com.jameslow.gui;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.datatransfer.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import com.jameslow.*;

public class Droppable implements DropTargetListener, DragSourceListener, DragGestureListener {
	public interface DroppableListener {
		public void addFilePath(String filepath);
		public void addFilePath(int index, String filepath);
		public void removeFilePath(int index);
		public void rearrangeFilePath(int index, int to);
	}
	
    DropTarget dropTarget;
    DragSource dragSource = DragSource.getDefaultDragSource();
	protected boolean dragging = false;
	protected int overIndex = -1;
	protected int dragIndex = -1;
	protected List<String> paths = new ArrayList<String>();
	protected JList list;
	protected JTable table;
	protected JComponent comp;
	protected List<DroppableListener> listeners = new ArrayList<DroppableListener>();
	
	private boolean allow_add = false; //Allow add to this list
	private boolean allow_rearrange = false; //Allow rearrange in this list
	private boolean allow_rejected_remove = false; //Remove items from this list if dragged over a rejecting component
	private boolean allow_file_transfer = true; //Allow file transfers (other programs / to a folder)
	private boolean allow_drag_away = true; //Allow drag away from this component

	public static DataFlavor LimegreenStringFlavor=null;
	public static DataFlavor LocalLimegreenStringFlavor=null;
	static {
		try {
			LimegreenStringFlavor = new DataFlavor(LimegreenString.class, "Non local LimegreenString");
			LocalLimegreenStringFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class="+LimegreenString.class.getName(), "Local LimegreenString");
			//LocalLimegreenStringFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + "; class=com.jameslow.gui.Droppable.LimegreenString", "Local LimegreenString");
		} catch(Exception e) {
			Main.Logger().info("Could not create Limegreen Flavours.");
		}
	}
	
	//Creation stuff
	public void init(JList list) {
		init(list,null);
	}
	public void init(JTable table) {
		init(table,null);
	}
	public void init(JList list, DroppableListener listener) {
		privateinit(listener,list);
		this.list = list;
	}
	public void init(JTable table, DroppableListener listener) {
		privateinit(listener,table);
		this.table = table;
	}
	private void privateinit(DroppableListener listener, JComponent comp) {
		this.dropTarget = new DropTarget(comp,this);
		if (listener != null) {
			listeners.add(listener);
		}
		this.comp = comp;
		dragSource.createDefaultDragGestureRecognizer(comp, DnDConstants.ACTION_COPY_OR_MOVE, this);	
	}
	public JComponent getComponent() {
		return comp;
	}
	public boolean getAllowAdd() {
		return allow_add;
	}
	public boolean getAllowRearrage() {
		return allow_rearrange;
	}
	public boolean getAllowRejectedRemove() {
		return allow_rejected_remove;
	}
	public boolean getAllowFileTransfer() {
		return allow_file_transfer;
	}
	public boolean getAllowDragAway() {
		return allow_drag_away;
	}
	public void setAllowAdd(boolean value) {
		allow_add = value;
	}
	public void setAllowRearrage(boolean value) {
		allow_rearrange = value;
	}
	public void setAllowRejectedRemove(boolean value) {
		allow_rejected_remove = value;
	}
	public void setAllowFileTransfer(boolean value) {
		allow_file_transfer = value;
	}
	public void setAllowDragAway(boolean value) {
		allow_drag_away = value;
	}
	
	//Clever stuff
	public int getSelectedIndex() {
		if (list != null) {
			return list.getSelectedIndex();
		} else {
			return table.getSelectedRow();
		}
	}
	public void setSelectedIndex(int index) {
		if (list != null) {
			list.setSelectedIndex(index);
		} else {
			table.setRowSelectionInterval(index, index);
		}
	}
	public boolean isSelectedIndex(int index) {
		if (list != null) {
			return list.isSelectedIndex(index);
		} else {
			return table.isRowSelected(index); 
		}
	}
	public void clearSelection() {
		if (list != null) {
			list.clearSelection();
		} else {
			table.clearSelection(); 
		}
	}
	public int[] getSelectedIndices() {
		if (list != null) {
			return list.getSelectedIndices();
		} else {
			return table.getSelectedRows();
		}
	}
	public int locationToIndex(Point point) {
		if (list != null) {
			return list.locationToIndex(point);
		} else {
			return table.rowAtPoint(point);
		}
	}
	public void beep() {
		if (list != null) {
			list.getToolkit().beep();
		} else {
			table.getToolkit().beep();
		}
	}
	public void addListener(DroppableListener listener) {
		listeners.add(listener);
	}
	public void removeListener(DroppableListener listener) {
		listeners.remove(listener);
	}
	
	//File path stuff
	public String getFilename(int index) {
		return FileUtils.getFilename(paths.get(index));
	}
	public String getSelectedFilePath() {
		return getFilepath(getSelectedIndex());
	}
	public String getFilepath(int index) {
		return paths.get(index);
	}
	public void addFilepaths(String[] paths) {
		addFilepaths(paths, getSelectedIndex());
	}
	public void addFilepaths(String[] paths, int index) {
		for (int i=0; i<paths.length; i++) {
			addFilepath(paths[i], index+i+1);
		}
	}
	public void addFilepath(String path) {
		addFilepath(path,getSelectedIndex());
	}
	public void addFilepath(String path, int index) {
		String filename = FileUtils.getFilename(path);
		if (index == -1 || index >= paths.size()) {
			paths.add(path);
			for (DroppableListener listener : listeners) {
				listener.addFilePath(filename);
			}
		} else {
			paths.add(index, path);
			for (DroppableListener listener : listeners) {
				listener.addFilePath(index, filename);
			}
			setSelectedIndex(index);
		}
	}
	public void removeSelectedFilepath() {
		if (getSelectedIndex() >= 0) {
			int i = 0;
			while (i < paths.size()) {
				if (isSelectedIndex(i)) {
					removeFilepath(i);
				} else {
					i++;
				}
			}
		}
	}
	public void removeAllFilepaths() {
		String[] paths = getFilepaths();
		for (int i=0; i<paths.length; i++) {
			removeFilepath(0);
		}
	}
	public void removeFilepath(int index) {
		paths.remove(index);
		for (DroppableListener listener : listeners) {
			listener.removeFilePath(index);
		}
	}
	public void removeFilepaths(int[] indices) {
		for(int i=0; i<indices.length; i++) {
			int index = indices[i];
			removeFilepath(index);
			for (int j=i; j<indices.length; j++) {
				int jndex = indices[j];
				if (jndex > index) {
					indices[j] = indices[j]-1;
				}
			}
		}
	}
	public void rearrangeFilepath(int index, int to) {
		addFilepath(getFilepath(index),to+1);
		if(index > to) {
			removeFilepath(index+1);
		} else {
			removeFilepath(index);
		}
		for (DroppableListener listener : listeners) {
			listener.rearrangeFilePath(index,to);
		}
	}
	public int getSize() {
		return paths.size();
	}
    public String[] getFilepaths() {
    	String[] result = new String[paths.size()];
    	for(int i=0; i<result.length; i++) {
    		result[i] = paths.get(i);
    	}
    	return result;
    }
	
	//Drag/drop stuff
	public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent) {
		if(allow_rejected_remove && !DragSourceDropEvent.getDropSuccess()) {
			try {
				Transferable tr = DragSourceDropEvent.getDragSourceContext().getTransferable();
				List<LimegreenString> lgstrings = (List<LimegreenString>) tr.getTransferData(LimegreenStringFlavor);
				int[] indices = new int[lgstrings.size()];
				int i = 0;
				for (LimegreenString lgs : lgstrings) {
					indices[i] = lgs.index;
					i++;
				}
				removeFilepaths(indices);
			} catch (UnsupportedFlavorException e) {
				Main.Logger().warning("UnsupportedFlavorException: "+e.getMessage());
			} catch (IOException e) {
				Main.Logger().warning("IOException: "+e.getMessage());
			}
		}
		dragging = false;
	}
	public void dragEnter(DragSourceDragEvent DragSourceDragEvent) {}
	public void dragExit(DragSourceEvent DragSourceEvent) {
		if (DragSourceEvent.getSource() == this) {
			/*
			try {
				Transferable tr = DragSourceEvent.getDragSourceContext().getTransferable();
				DataFlavor[] flavors = tr.getTransferDataFlavors();
				Object[] lstrings = (Object[]) tr.getTransferData(LimelightStringFlavor);
				List<Integer> l = new ArrayList<Integer>();
				for(Object o : lstrings) {
					if (o != null) {
						LimelightString ls = (LimelightString) o;
						l.add(ls.index);					
					}
				}
				int[] indices = new int[l.size()];
				for (int i=0; i<indices.length; i++) {
					indices[i] = l.get(i);
				}
				setSelectedIndices(indices);
			} catch (UnsupportedFlavorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			*/
		}
	}
	public void dragOver(DragSourceDragEvent DragSourceDragEvent) {}
	public void dropActionChanged(DragSourceDragEvent DragSourceDragEvent) {}
	public void dragEnter (DropTargetDragEvent dropTargetDragEvent) {
		dropTargetDragEvent.acceptDrag(DnDConstants.ACTION_COPY_OR_MOVE);
		overIndex = locationToIndex(dropTargetDragEvent.getLocation());
		setSelectedIndex(overIndex);
	}
	public void dragExit (DropTargetEvent dropTargetEvent) {
		 this.overIndex = -1;
	}
	public void dragOver (DropTargetDragEvent dropTargetDragEvent) {
		if (allow_add || allow_rearrange) {
			int overIndex = locationToIndex(dropTargetDragEvent.getLocation());
			if (overIndex == -1) {
				clearSelection();
			} else if(overIndex != this.overIndex) {
				// If the value has changed from what we were previously over
				// then change the selected object to the one we are over; this 
				// is a visual representation that this is where the drop will occur
				setSelectedIndex(overIndex);
			}
			this.overIndex = overIndex;
		}
	}
	public void dropActionChanged (DropTargetDragEvent dropTargetDragEvent) {}
	public synchronized void drop (DropTargetDropEvent dropTargetDropEvent) {
		try {
			Transferable tr = dropTargetDropEvent.getTransferable();
			if (allow_rearrange && dragging && tr.isDataFlavorSupported(LimegreenStringFlavor)) {
				dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				DataFlavor[] flavors = tr.getTransferDataFlavors();
				List<LimegreenString> lgstrings = (List<LimegreenString>) tr.getTransferData(LimegreenStringFlavor);
				int i = 1;
				int j = 0;
				for (LimegreenString lgs : lgstrings) {
					if (lgs != null) {
						if (dragging) {
							rearrangeFilepath(lgs.index,overIndex+j);
							if (lgs.index > overIndex) {
								j++;
							}
						} else {
							File file = new File(lgs.filepath);
							if (file.isFile()) {
								addFilepath(lgs.filepath,overIndex+i++);
							}
						}
					}
				}
				dropTargetDropEvent.getDropTargetContext().dropComplete(true);
			} else if (allow_add && tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				List<File> filelist = (List<File>) tr.getTransferData(DataFlavor.javaFileListFlavor);
				int i = 1;
				for (File file : filelist) {
					if (file.isFile()) {
						addFilepath(file.getAbsolutePath(),overIndex+i);
					} else if (file.isDirectory()) {
						String[] files = file.list(FileUtils.getHiddenDirFilter());
						int j = 0;
						for (String filepath : files) {
							addFilepath(file.getAbsolutePath() + Main.OS().fileSeparator() + filepath,overIndex+j++);
						}
					}
					i++;
				}
				dropTargetDropEvent.getDropTargetContext().dropComplete(true);
			} else {
				Main.Logger().info("Rejected drop");
				dropTargetDropEvent.rejectDrop();
			}
		} catch (IOException io) {
			Main.Logger().warning("IOException: "+io.getMessage());
			dropTargetDropEvent.rejectDrop();
		} catch (UnsupportedFlavorException ufe) {
			Main.Logger().warning("Unsupported drag flavour: "+ufe.getMessage());
			dropTargetDropEvent.rejectDrop();
		}
	}
	public void dragGestureRecognized(DragGestureEvent dragGestureEvent) {
		dragging = true;
		dragIndex = getSelectedIndex();
		if (dragIndex != -1) {
			//FileSelection transferable = new FileSelection(new File(getFilepath(getSelectedIndex())));
			int[] selected = getSelectedIndices();
			FileSelection transferable = new FileSelection(allow_file_transfer);
			for (int i : selected) {
				LimegreenString lgs = new LimegreenString();
				lgs.filepath = getFilepath(i);
				lgs.index = i;
				transferable.addLimegreenString(lgs);
			}
			dragGestureEvent.startDrag(DragSource.DefaultCopyDrop,transferable,this);
		} else {
			beep();
		}
	}
	public class FileSelection implements Transferable {
		final static int LIMEGREEN = 0;
		final static int FILE = 1;
        final static int STRING = 2;
        final static int PLAIN = 3;	
        DataFlavor flavors[];
        private boolean allow_file_transfer;
        private List<LimegreenString> lgstrings = new Vector<LimegreenString>();
		
        public FileSelection(boolean allow_file_transfer) {
        	this.allow_file_transfer = allow_file_transfer;
        	if (allow_file_transfer) {
        		flavors = new DataFlavor[4];
        		flavors[LIMEGREEN] = LimegreenStringFlavor;
        		flavors[FILE] = DataFlavor.javaFileListFlavor;
        		flavors[STRING] = DataFlavor.stringFlavor;
        		flavors[PLAIN] = DataFlavor.plainTextFlavor;
        	} else {
        		flavors = new DataFlavor[1];
        		flavors[LIMEGREEN] = LimegreenStringFlavor;
        	}
        }
        //{
		//	
        //}
        /* Returns the array of flavors in which it can provide the data. */
        public synchronized DataFlavor[] getTransferDataFlavors() {
			return flavors;
        }
        public boolean isDataFlavorSupported(DataFlavor flavor) {
        	for (DataFlavor flavortype : flavors) {
        		if (flavortype.equals(flavor)) {
        			return true;
        		}
        	}
        	return false;
        }
        public void addLimegreenString(LimegreenString lgs) {
        	lgstrings.add(lgs);
        }
        public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        	if (flavor.equals(flavors[LIMEGREEN])) {
        		return lgstrings;
        	} else if (allow_file_transfer) {
        		if (flavor.equals(flavors[FILE])) {
        			List<File> list = new Vector<File>();
        			for (LimegreenString lgs : lgstrings) {
        				list.add(new File(lgs.filepath));
        			}
        			return list;
				} else if (flavor.equals(flavors[PLAIN]) || flavor.equals(flavors[STRING])) {
					String result = "";
					final String ls = Main.OS().lineSeparator();
					for (LimegreenString lgs : lgstrings) {
        				result = result + lgs.filepath + ls;
        			}
					if (flavor.equals(flavors[PLAIN])) {
						//010-03-31 17:50:16.157 java[48703:80f] Couldn't convert path "/Users/James/Documents/Worship/Lyrics/A New Commandment.txt" to an FSRef to put on the pasteboard.
						return new StringReader(result);
					} else {
						return result;
					}
				} else {
					throw new UnsupportedFlavorException(flavor);
				}
        	} else {
        		throw new UnsupportedFlavorException(flavor);
        	}
        }
    }
}