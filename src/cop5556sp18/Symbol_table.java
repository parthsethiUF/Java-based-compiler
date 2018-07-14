package cop5556sp18;


import java.util.*;
import cop5556sp18.AST.*;
import cop5556sp18.node;

public class Symbol_table {
	
	
	
	
	Stack<Integer> scope_stack = new Stack<Integer>();
	HashMap<String, ArrayList<node>> hm = new HashMap<String, ArrayList<node>>();
	int current_scope, next_scope;
	
	public Symbol_table() {
		this.current_scope = 0;
		this.next_scope = 0;
		scope_stack.push(0);
	}	
	
	public void enterScope(){
		current_scope = next_scope++;
		scope_stack.push(current_scope);
	}
	public void leaveScope(){
		scope_stack.pop();
		current_scope = scope_stack.peek();	
	}
	
	public boolean insert(String s, Declaration dec){
		ArrayList<node> temp = new ArrayList<node>();
		node t = new node(current_scope, dec);
		if(hm.containsKey(s)){
			temp = hm.get(s);
			for(node tb : temp){
				if(tb.scope == current_scope)
					return false;
			}
		}
		temp.add(t);
		hm.put(s, temp);
		return true;
	}
	
	
	public boolean check(String s, Declaration dec) {
			ArrayList<node> temp = new ArrayList<node>();
			node t = new node(current_scope, dec);
			if(hm.containsKey(s)){
				temp = hm.get(s);
				for(node tb : temp){
					if(tb.scope == current_scope)
						return true;
				}
			}
			return false;
	}
	public Declaration lookup(String s){
		if(!hm.containsKey(s))
			return null;
		
		Declaration d=null;
		ArrayList<node> look = hm.get(s);
		for(int i=look.size()-1;i>=0;i--)
		{
			int temp_scope = look.get(i).getScope();
			if(scope_stack.contains(temp_scope))
			{
				d = look.get(i).getDec();
				break;
			}
		}
		return d;
	}
}
