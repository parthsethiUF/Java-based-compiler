package cop5556sp18;

import cop5556sp18.AST.Declaration;

public class node{
	int scope;
	Declaration d;
	
	public node(int scope_temp, Declaration dec){
		this.scope = scope_temp;
		this.d = dec;
	}
	
	public int getScope(){
		return scope;
	}
	
	public Declaration getDec(){
		return d;
	}
}