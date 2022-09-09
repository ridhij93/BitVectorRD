package bitvectorrd;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import soot.Pack;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.options.Options;

public class Driver {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if(args.length==0)
		{
			System.err.println("Usage: java Driver [options] classname");
			System.exit(0);
		}
		/*Options.v().setPhaseOption("jb", "use-original-names:true");
        Options.v().set_keep_line_number(true);
		Pack jtp=PackManager.v().getPack("jtp");
		jtp.add(new Transform("jtp.instrumenter", new ReachingDefinitionWrapper()));
		Options.v().set_output_format(Options.output_format_jimple);
		//Scene.v().extendSootClassPath(System.getProperty("user.dir")+System.getProperty("file.separator")+"bin");
		String path = Scene.v().getSootClassPath();
		Scene.v().setSootClassPath(path);
		soot.Main.main(args);*/
		
		Options.v().setPhaseOption("jb", "use-original-names:true"); 
        Pack jtp = PackManager.v().getPack("jtp");
        jtp.add(new Transform("jtp.instrumenter", new ReachingDefinitionWrapper()));
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_output_dir("/home/dhriti/Dropbox/new-workspace/BitVectorRidhi/sootOutput");
       
        String path = Scene.v().getSootClassPath();
        System.out.println("\npath:" + path + "\n");
		  
		Scene.v().extendSootClassPath(System.getProperty("user.dir")+System.getProperty("file.separator")+"bin");
		List<String>  sootArgs = new LinkedList(Arrays.asList(args));
		sootArgs.add("--keep-line-number");
		String[] argsArray = sootArgs.toArray(new String[0]);
		soot.Main.main(argsArray);
	}
}