package bitvectorrd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import soot.Value;

import soot.toolkits.graph.UnitGraph;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.Stmt;
import soot.tagkit.LineNumberTag;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.AbstractFlowSet;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.toolkits.scalar.Pair;
import soot.util.Chain;

public class ReachingDefinitionAnalysis extends ForwardFlowAnalysis {

    Body b;
    FlowSet inval, outval;

    public ReachingDefinitionAnalysis(UnitGraph g) {
        super(g);
        b = g.getBody();
        doAnalysis();
    }

    @Override
    protected void flowThrough(Object in, Object unit, Object out) {
        inval = (FlowSet) in;
        outval = (FlowSet) out;
        Stmt u = (Stmt) unit; 
        inval.copy(outval);
        String var;
        LineNumberTag tag = (LineNumberTag) u.getTag("LineNumberTag");
        
        if(u instanceof AssignStmt || u instanceof IdentityStmt) {
	        System.out.println("\nStmt: " + u.toString() + " at " + tag);
	                
	        System.out.println("InSet: ");
	        Iterator it = inval.iterator();
	        while (it.hasNext()) {
	            Pair inv = (Pair<String, String>) it.next();
	            System.out.print("[" + inv.getO1().toString() + ", " + inv.getO2().toString() + "]  ");
	        }
	        System.out.println();
        }
	        
        //Kill the var which is defined
        if (u instanceof AssignStmt) {
            Iterator<ValueBox> defIt = u.getDefBoxes().iterator();
            ArrayList<Pair<String, String>> pairsToBeKilled = new ArrayList<Pair<String, String>>();
            if(defIt.hasNext()) {
            	String definedVar = defIt.next().getValue().toString();
            	Iterator itOut = outval.iterator();
                while (itOut.hasNext()) {
                    Pair p = (Pair<String, String>) itOut.next();
                    if(p.getO1().toString().equals(definedVar)) {
                        pairsToBeKilled.add(p);
                    }
                }
            }
            Iterator<Pair<String, String>> pIt = pairsToBeKilled.iterator();
            while (pIt.hasNext()) {
            	Pair<String, String> p = pIt.next();
            	outval.remove(p);
            	System.out.println("Killing " + p.getO1() + " " + p.getO2());
            }
        }
        //Gen the variable which s defined
        if (u instanceof AssignStmt) {
            Iterator<ValueBox> defIt = u.getDefBoxes().iterator();
            while (defIt.hasNext()) {
                String first = defIt.next().getValue().toString();
                if (!first.contains("$")) {
                    Pair p = new Pair(first, tag.toString());
                    outval.add(p);
                    System.out.println("Generating " + p.getO1() + " " + p.getO2());
                }
            }
        }
        if(u instanceof AssignStmt || u instanceof IdentityStmt) {
	        System.out.println("\nOutset: ");
	        Iterator itOut = outval.iterator();
	        while (itOut.hasNext()) {
	            Pair inv = (Pair<String, String>) itOut.next();
	            System.out.print("[" + inv.getO1().toString() + ", " + inv.getO2().toString() + "]  ");
	        }
	        System.out.println();
        }
    }

    @Override
    protected void copy(Object source, Object dest) {
        FlowSet srcSet = (FlowSet) source;
        FlowSet destSet = (FlowSet) dest;
        srcSet.copy(destSet);
        Iterator it = destSet.iterator();
    }

    @Override
    protected Object entryInitialFlow() {
        ArraySparseSet as = new ArraySparseSet();
        Chain<Local> locals = this.b.getMethod().getActiveBody().getLocals();
        Iterator<Local> i2 = locals.iterator();
        while (i2.hasNext()) {
            Local ll = i2.next();
            String name = ll.getName();
            if (!((name.equals("this")) || (name.charAt(0) == '$'))) {
                Pair<String, String> p = new Pair<String, String>(name, "?");
                as.add(p);
                System.out.println("Adding in entryInitial flow: " + p);
            }
        }
        return as;
    }

    @Override
    protected void merge(Object in1, Object in2, Object out) {
        FlowSet inval = (FlowSet) in2;
        FlowSet outSet = (FlowSet) out;
        // May analysis
        inval.union(inval, outSet);

    }

    @Override
    protected Object newInitialFlow() {
        ArraySparseSet as = new ArraySparseSet();
        return as;
    }

}
