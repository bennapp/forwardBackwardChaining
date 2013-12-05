import java.util.*;
import java.io.*;

public class Sentence{
	public LinkedList<Operator> opList;
	public boolean impForm;

	public Sentence(LinkedList<Operator> opList, boolean impForm){
		this.opList = opList;
		this.impForm = impForm;
	}

	public Sentence(){
		this.opList = null;
		this.impForm = false;
	}

}