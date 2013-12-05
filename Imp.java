import java.util.*;
import java.io.*;

public class Imp extends Operator{
	public Imp(Variable first, Variable second){
		this.first = first;
		this.second = second;
		this.token = "=>";
	}

	public void op(){
		// System.out.println("um what?");

	}
}