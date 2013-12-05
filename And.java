import java.util.*;
import java.io.*;

public class And extends Operator {
	public And(Variable first, Variable second){
		this.first = first;
		this.second = second;
		this.token = "^";
	}
	public And(){
		this.token = "^";
		this.first = null;
		this.second = null;
	}


	public void op(){
		if(this.first.set && this.second.set){
			if(this.first.getValue() && this.second.getValue()){
				this.second.setValue(true);
			} else{
				this.second.setValue(false);
			}
		} else {
			this.second.setValue(false);
		}
	}
}