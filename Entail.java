import java.util.*;
import java.io.*;

public class Entail{
	public static BufferedReader br;
	public static String currentLine;
	public static String sentencesS = "";
	public static String[] sentencesA;
	public static LinkedList<String> facts = new LinkedList<String>();
	public static Hashtable<String, Boolean> factsTable = new Hashtable<String, Boolean>();
	public static LinkedList<String> notfacts = new LinkedList<String>();
	public static LinkedList<Sentence> sentences = new LinkedList<Sentence>();
	public static LinkedList<String> agenda = new LinkedList<String>();
	public static Hashtable<String, Boolean> entailed = new Hashtable<String, Boolean>();
	public static Hashtable<String, Boolean> setSymbol = new Hashtable<String, Boolean>(); 
	public static Hashtable<String, Boolean> valSymbol = new Hashtable<String, Boolean>();
	public static LinkedList<String> toPush = new LinkedList<String>();


	public static void print(Object stringOrMore){
		System.out.println(stringOrMore);
	}

	public static boolean notOp(String check){
		return ((!check.contains("^")) && (!check.contains("v")) && (!check.contains("=>")));
	}

	public static void addSentences(LinkedList<String> sentencesList){
		Iterator<String> sentenceIt = sentencesList.iterator();
		while(sentenceIt.hasNext()){
			String sent = sentenceIt.next();
			LinkedList<Operator> ops = new LinkedList<Operator>();
			// print(sentencesA[i]);
			//facts
			//print("vvvv");
			//print(sent);
			//print("^^^^");
			if(notOp(sent)){
				if(sent.charAt(0)=='~'){
					notfacts.add(sent);
					factsTable.put(sent, true);
				}else{
					facts.add(sent);
					factsTable.put(sent, true);
				}
			} else {
				//sentencesS
				String[] tokens = sent.split(" ");
				Variable[] variables = new Variable[tokens.length];
				for(int j=0;j<tokens.length;j++){
					if(notOp(tokens[j])){
						if(tokens[j].charAt(0) == '~'){
							variables[j] = new Variable(tokens[j], false, true);
							if(tokens[j].equals("false")){
								variables[j] = new Variable("true");
							}
							if(tokens[j].equals("true")){
								variables[j] = new Variable("false");
							}
						}else{
							variables[j] = new Variable(tokens[j], false, false);
							if(tokens[j].equals("false")){
								variables[j] = new Variable("false");
							}
							if(tokens[j].equals("true")){
								variables[j] = new Variable("true");
							}
						}
					}
				}
				for(int j=0;j<tokens.length;j++){
					if(tokens[j].equals("^")){
						ops.add(new And(variables[j-1], variables[j+1]));	
					}
					if(tokens[j].equals("v")){
						ops.add(new Or(variables[j-1], variables[j+1]));	
					}
					if(tokens[j].equals("=>")){
						ops.add(new Imp(variables[j-1], variables[j+1]));	
					}
				}
			sentences.add(new Sentence(ops, false));				
			}
		}
	}
	public static void deParenSentence(String sentence, int depth, LinkedList<String> toPush, String build, boolean ready){
		// print("" + depth);
		// print("." + sentence);
		// print("." + build);
		//if(sentence.equals("")){
		//	print("i pushed = " + build);
		//	toPush.push(build);
		//	return toPush;
		//}
		if(sentence.length()>0 && sentence.charAt(0)=='('){
			//print("(");
			deParenSentence(sentence.substring(1), (depth +1), toPush, build, true);
		} else if(sentence.length()>0 && sentence.charAt(0)==')'){
			//print(")");
			deParenSentence(sentence.substring(1), (depth -1), toPush, build, true);
		} else if(depth == 0 && ready){
			//print("zero");
			//if(build.equals("")){
			//}else{
			//	print("i pushed = " + build);
			//	toPush.push(build);
			//}
			String rest = "";
			String[] splitC = sentence.split(" \\^ ");
			//print("split c length = " + splitC.length);
			if(splitC.length > 1){
				int i;
				for (i =1;i<splitC.length-1;i++){
					rest += splitC[i]+" ^ ";
				}
				rest += splitC[i];
				//print("pushed = " + build);
				//print("rest = " + rest);
				toPush.push(build);
				deParenSentence(rest, depth, toPush, "", false);
			} else{
				//print("pushed => " + build);
				toPush.push(build);
			}
			
			//else{	print("i pushed = " + sentence);
			//	toPush.push(sentence);
			//	return toPush;
			//}
			//if(!splitC[0].equals("")){
			//	toPush.push(splitC[0]);
			//	print("i pushed = " + splitC[0]);
			//	deParenSentence(rest, depth, toPush, "");
			//}
			

			//if(sentence.length() > 3){
			//	String rest = sentence.substring(3);
			//	toPush = deParenSentence(rest, depth, toPush, "");
			//}else{
			//	// print(" !" + depth);
			//	// print(" !" + sentence);
			//	// print(" !" + build);
			//	toPush.push(sentence);
			//}
		} else {
			//print("char");
			build += sentence.charAt(0);
			deParenSentence(sentence.substring(1), depth, toPush, build, true);
		}
	}
	//                         (P => Q) ^ (L ^ M => P) ^ A

	//( (~B v P) v M ) ^ ( B v ~P ) ^ ( B v ~M ) ^ ~B ^ ( (A) ^ B => C )
	// A ^ ((B)) ^ (A ^ B => C)
	// (A ^ B => C) ^ D
	public static LinkedList<String> removeParens(String[] sentencesA){
		LinkedList<String> sentenceList = new LinkedList<String>();
		for(int i=0;i<sentencesA.length;i++){
			if(sentencesA[i].contains("(")){
				toPush.clear();
				deParenSentence(sentencesA[i], 0, toPush, "", false);
				Iterator<String> toPushi = toPush.iterator();
				while(toPushi.hasNext()){
					// print("? " +toPushi.next());
					sentenceList.push(toPushi.next());
				}
			} else {
				sentenceList.push(sentencesA[i]);
			}
		}
		return sentenceList;
	}

	public static void main(String[] args){
		try{
			String alg = args[0];
			String kBFile = args[1];
			String querySymbol = args[2];
			br = new BufferedReader(new FileReader(kBFile));
			while ((currentLine = br.readLine()) != null) {
				sentencesS += currentLine + "@";
			}
			sentencesA = sentencesS.split("@");
			LinkedList<String> sentenceList = new LinkedList<String>();
			sentenceList = removeParens(sentencesA);
			addSentences(sentenceList);

			//convert the sentences to implicative form
			LinkedList<Sentence> toRemove = new LinkedList<Sentence>();
			LinkedList<Sentence> toAdd = new LinkedList<Sentence>();
			Iterator<Sentence> i = sentences.iterator();
			while(i.hasNext()){
				Sentence temp = i.next();

				//check if imp form
				//asssuming horn form => as last op then body is horn form
				if(temp.opList.getLast().token.equals("=>")){
					temp.impForm = true;
				}

				Variable head = null;
				Operator opHead = null;
				Operator opHeadFirst = null;
				boolean allOr = true;
				boolean noHead = true;
				Iterator<Operator> j = temp.opList.iterator();
				while(j.hasNext()){
					Operator tempOp = j.next();
					if(!tempOp.token.equals("v")){
						allOr = false;
					}
					if(!(tempOp.first.not)){
						head = tempOp.first;
						opHeadFirst = tempOp;
						noHead = false;
					}
					if(!tempOp.second.not){
						head = tempOp.second;
						opHead = tempOp;
						noHead = false;
					}
				}

				if(noHead){
					Or addFalse = new Or(temp.opList.getLast().second, new Variable("false"));
					opHead = addFalse;
					temp.opList.add(addFalse);
				}

				//convert to imp
				Iterator<Operator> k = temp.opList.iterator();
				if(allOr){
					Sentence impSentence = new Sentence();
					
					Variable impFirst = new Variable();
					Variable impSecond = new Variable();
					Operator impOp = new And();

					LinkedList<Operator> impOps = new LinkedList<Operator>();
					toRemove.add(temp);
					while(k.hasNext()){
						Operator tempOp = k.next();
						if(tempOp != opHead){
							impFirst = new Variable();
							impSecond = new Variable();

							impFirst.name = tempOp.first.name.substring(1);
							impFirst.value = tempOp.first.value;
							impFirst.set = tempOp.first.set;
							impFirst.not = false;
							//
							impSecond.name = tempOp.second.name.substring(1);
							impSecond.value = tempOp.second.value;
							impSecond.set = tempOp.second.set;
							impSecond.not = false;

							impOp = new And(impFirst, impSecond);
							impOp.token = "^";
							if(impOp.first.name.equals("")){
							} else {
								impOps.add(impOp);
							}

						} else{
							if(opHeadFirst != null){
								impFirst = new Variable();
								impSecond = new Variable();
								impFirst.name = tempOp.first.name.substring(1);
								impFirst.value = tempOp.first.value;
								impFirst.set = tempOp.first.set;
								impFirst.not = false;
								impSecond.name = opHeadFirst.second.name.substring(1);
								impSecond.value = opHeadFirst.second.value;
								impSecond.set = opHeadFirst.second.set;
								impSecond.not = false;
								impOp = new And(impFirst, impSecond);
								impOp.token = "^";
								impOps.add(impOp);
							}
						}
					}

					impSentence.opList = impOps;

					if(noHead){
						impFirst = impOps.getLast().second;
						Operator nImp = new Imp(impFirst, new Variable("false"));
						impOps.add(nImp);
						impSentence.impForm = true;
					} else {
						impFirst = impOps.getLast().second;
						impSecond = head;
						Operator nImp = new Imp(impFirst, impSecond);
						impOps.add(nImp);
						impSentence.impForm = true;
					}
						toAdd.add(impSentence);
						// String sentenceTest = "";
						// while(!impSentence.opList.isEmpty()){
						// Operator tempOP = impSentence.opList.pop();
							// sentenceTest += tempOP.first.name + " " + tempOP.token + " " + tempOP.second.name;
							//temp.op();
						// }
						// print(sentenceTest);
				}
			}
			Iterator<Sentence> removeSentences = toRemove.iterator();
			while(removeSentences.hasNext()){
				Sentence remove = removeSentences.next();
				sentences.remove(remove);
			}
			Iterator<Sentence> addSentences = toAdd.iterator();
			while(addSentences.hasNext()){
				Sentence add = addSentences.next();
				sentences.add(add);
			}

			agenda = facts;
			Iterator<String> a = agenda.iterator();
			while(a.hasNext()){
				String symbol = a.next();
				setSymbol.put(symbol, true);
				valSymbol.put(symbol, true);
			}
			Iterator<String> b = notfacts.iterator();
			while(b.hasNext()){
				String symbol = b.next();
				setSymbol.put(symbol, true);
				valSymbol.put(symbol.substring(1), false);
			}

			if(alg.equals("forward")){
				while(!agenda.isEmpty()){
					LinkedList<Sentence> clauses = new LinkedList<Sentence>();
					Iterator<Sentence> clauseInit = sentences.iterator();
					while(clauseInit.hasNext()){
						clauses.push(clauseInit.next());
					}

					String fact = agenda.pop();

					if(!fact.equals("false")){
						entailed.put(fact, true);
					}
					Iterator<Sentence> clauseI = clauses.iterator();
					while(clauseI.hasNext()){
						Sentence clause = clauseI.next(); 

						Iterator<Operator> oi = clause.opList.iterator();
						while(oi.hasNext()){
							Operator operator = oi.next();
							if(setSymbol.containsKey(operator.first.name)){
								operator.first.set(true);
							}
							if(setSymbol.containsKey(operator.second.name)){
								operator.second.set(true);
							}
							if(valSymbol.containsKey(operator.first.name)){
								operator.first.setValue(valSymbol.get(operator.first.name));
							}
							if(valSymbol.containsKey(operator.second.name)){
								operator.second.setValue(valSymbol.get(operator.second.name));
							}
						}

						oi = clause.opList.iterator();
						Operator headOp = null;
						while(oi.hasNext()){
							Operator operator = oi.next();
							operator.op();
							if(operator.token.equals("=>")){
								headOp = operator;
							}
						}
						if(headOp!= null){ //I don't think you can have C ^ D => ~E
							if(headOp.first.getValue()){
								setSymbol.put(headOp.second.name, true);
								valSymbol.put(headOp.second.name, true);

								sentences.remove(clause);
								agenda.add(headOp.second.name);
								printSentence(clause);
							}
							
						}
					}
				}

				if(entailed.containsKey(querySymbol)){
					print("True");
				} else{
					print("False");
				}
			} else if(alg.equals("backward")) { //if alg
				boolean output = false;
				Hashtable<String, Sentence> sentenceTable = new Hashtable<String, Sentence>();

				Iterator<Sentence> isentences = sentences.iterator();
				while(isentences.hasNext()){
					Sentence s = isentences.next();
					if(s.opList.getLast().token.equals("=>")){
						sentenceTable.put(s.opList.getLast().second.name, s);
					}
				}

				output = prove(querySymbol, factsTable, sentenceTable);

				if(output){
					print("True");
				} else {
					print("False");
				}
			}
			
			//implement alg
			//implment alg 2
			//deal with parens

			//Converting works
			// Iterator<Sentence> test = sentences.iterator();
			// while(test.hasNext()){
			// 	Sentence testS = test.next();
			// 	Iterator<Operator> opTest = testS.opList.iterator();
			// 	String sentenceTest = "";
			// 	while(opTest.hasNext()){
			// 		Operator testOP = opTest.next();

			// 		sentenceTest += testOP.first.name + " " + testOP.token + " " + testOP.second.name + ".";
			// 	}
			// 	print(sentenceTest);
			// }

			//AND works
			//OR works?? except if it is not set but the value is true?? then it will pass on true eval
			//operating on an op changes its second var which will alter the next ops first var
			// sentences.pop();
			// sentences.pop();
			// Sentence testS = sentences.pop();
			// int i = 0;
			// while(!testS.opList.isEmpty()){
			// 	Operator temp = testS.opList.pop();
			// 	if(i==0){
			// 		temp.first.set = true;
			// 		temp.first.setValue(false);
			// 		temp.second.set = true;
			// 		temp.second.setValue(true);
			// 		print(temp.first.name);
			// 		print(temp.token);
			// 		print(temp.second.name);
			// 		temp.op();
			// 	}
			// 	if(i==1){
			// 		print(temp.first.getValue());
			// 	}
			// 	i++;
			// }

			//while(!facts.isEmpty()){//checks out
			//	print(facts.pop());
			//}

		} catch (IOException e){
			System.err.println("Try <algorithm> <knowledgeBaseFile.txt> <querySybol>");
			e.printStackTrace();
		}
	}

	public static boolean prove(String q, Hashtable<String, Boolean> facts, Hashtable<String, Sentence> sTable){
		boolean output = false;
		if(facts.containsKey(q)){
			output = true;
		} else {
				boolean allTrue = true;
			if(sTable.containsKey(q)){
				//iterate through all of the children if they are prove then add q to facts
				Sentence s = sTable.get(q);
				printSentence(s);
				Iterator<Operator> opit = s.opList.iterator();
				String newFact = s.opList.getLast().second.name;
				LinkedList<Sentence> sPop = new LinkedList<Sentence>();
				while(opit.hasNext()){
					Operator op = opit.next();
					if(!prove(op.first.name, facts, sTable)){
						allTrue = false;
					}
				}
				if(allTrue){
					facts.put(newFact, true);
					sPop.push(s);
				}
				return allTrue;
			} else {
				output = false;
			}
		}
		return output;
	}

	public static void printSentence(Sentence clause){
		Iterator<Operator> opTest = clause.opList.iterator();
		String sentenceTest = "";
		while(opTest.hasNext()){
			Operator testOP = opTest.next();
			sentenceTest += testOP.first.name + " " + testOP.token + " " + testOP.second.name + "%";
		}
		String[] sentenceParse = sentenceTest.split("%");
		String sentenceout =sentenceParse[0];
		for(int l=1;l<sentenceParse.length;l++){
			sentenceout+= sentenceParse[l].substring(1, sentenceParse[l].length());
		}
		print(sentenceout);
	}
}