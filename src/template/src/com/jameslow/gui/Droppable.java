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
	
	private boolean allow_add = false;
	private boolean allow_rearrange = false;
	private boolean allow_rejected_remove = false;
	private boolean allow_file_transfer = true;

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
	public void init(DroppableListener listener, JList list) {
		privateinit(listener,list);
		this.list = list;
	}
	public void init(DroppableListener listener, JTable table) {
		privateinit(listener,table);
		this.table = table;
	}
	private void privateinit(DroppableListener listener, JComponent comp) {
		this.dropTarget = new DropTarget(comp,this);
		listeners.add(listener);
		this.comp = comp;
		dragSource.createDefaultDragGestureRecognizer(comp, DnDConstants.ACTION_COPY_OR_MOVE, this);	
	}
	public JComponent getComponent() {
		return comp;
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
	
	//Drag/drop stuff
	public void dragDropEnd(DragSourceDropEvent DragSourceDropEvent) {
		dragging = false;
	}
	public void dragEnter(DragSourceDragEvent DragSourceDragEvent) {}
	public void dragExit(DragSourceEvent DragSourceEvent) {}
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
	public void dropActionChanged (DropTargetDragEvent dropTargetDragEvent) {}
	
	public synchronized void drop (DropTargetDropEvent dropTargetDropEvent) {
		try {
			Transferable tr = dropTargetDropEvent.getTransferable();
			if (tr.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				List fileList = (List) tr.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator iterator = fileList.iterator();
				int i = 1;
				while (iterator.hasNext()) {
					File file = (File)iterator.next();
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
			} else if (tr.isDataFlavorSupported(LimegreenStringFlavor)) {
				dropTargetDropEvent.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				DataFlavor[] flavors = tr.getTransferDataFlavors();
				Object[] lstrings = (Object[]) tr.getTransferData(LimegreenStringFlavor);
				int i = 1;
				int j = 0;
				for(Object o : lstrings) {
					if (o != null) {
						LimegreenString ls = (LimegreenString) o;
						if (dragging) {
							rearrangeFilepath(ls.index,overIndex+j);
							if (ls.index > overIndex) {
								j++;
							}
						} else {
							File file = new File(ls.filepath);
							if (file.isFile()) {
								addFilepath(ls.filepath,overIndex+i++);
							}
						}
					}
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
			FileSelection transferable = new FileSelection();
			for (int i : selected) {
				System.out.println("Here: "+i+" "+paths.size());
				LimegreenString ls = new LimegreenString();
				ls.filepath = getFilepath(i);
				ls.index = i;
				transferable.add(ls);
			}
			dragGestureEvent.startDrag(DragSource.DefaultCopyDrop,transferable,this);
		} else {
			beep();
		}
	}
    public String[] getFilepaths() {
    	String[] result = new String[paths.size()];
    	for(int i=0; i<result.length; i++) {
    		result[i] = paths.get(i);
    	}
    	return result;
    }
	
	public class FileSelection extends Vector implements Transferable {
        /*
		final static int FILE = 0;
        final static int STRING = 1;
        final static int PLAIN = 2;
		DataFlavor flavors[] = {DataFlavor.javaFileListFlavor,
                                DataFlavor.stringFlavor,
                                DataFlavor.plainTextFlavor};
		*/
        DataFlavor flavors[] = {LimegreenStringFlavor};
		
        //public FileSelection(File file)
        //{
		//	addElement(file);
        //}
        /* Returns the array of flavors in which it can provide the data. */
        public synchronized DataFlavor[] getTransferDataFlavors() {
			return flavors;
        }
        public boolean isDataFlavorSupported(DataFlavor flavor) {
        	return flavor.equals(flavors[0]);
        }
        public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (flavor.equals(flavors[0])) {
				return this.elementData;
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
        }
		/*
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            boolean b  = false;
            b |= flavor.equals(flavors[FILE]);
            b |= flavor.equals(flavors[STRING]);
            b |= flavor.equals(flavors[PLAIN]);
        	return (b);
        }
        public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (flavor.equals(flavors[FILE])) {
				return this;
			} else if (flavor.equals(flavors[PLAIN])) {
				return new StringReader(((File)elementAt(0)).getAbsolutePath());
			} else if (flavor.equals(flavors[STRING])) {
				return((File)elementAt(0)).getAbsolutePath();
			} else {
				throw new UnsupportedFlavorException(flavor);
			}
        }
		*/
    }
}