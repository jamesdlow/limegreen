import com.jameslow.*;

public class @ProjectName@ extends Main {
	public @ProjectName@(String args[]) {
		super(args,null,null,null,null,null,null,null);
		//super(args,null,null,@ProjectName@Settings.class.getName(),@ProjectName@Window.class.getName(),null,null,@ProjectName@Pref.class.getName());
	}
	public static void main(String args[]) {
		instance = new @ProjectName@(args);
	}
}