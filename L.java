import java.util.Scanner;
import java.util.ArrayList;

//package linkerlab;

public class Linker{

	public static boolean isAlpha(String name) {
    	return name.matches("[a-zA-Z]+");
	}

	public static void main(String[] args){
		//Scanner scan = new Scanner(new BufferedInputStream(System.in));
		Scanner scan = new Scanner(System.in);
		
		//prompt the user
		//System.out.println("Give the input set: ");
		
		//read the String input
		String input;
		String string = "";
		while(scan.hasNext()){
			input = scan.nextLine();
			string = string + input + " " ;
			
		}
		//normalize the string
		//remove any duplicate whitespaces
		//add " " at end b/c new line spaces at end went missing
		//string = string.replaceAll("^\\s+", ""); //left trim
		//string = string.replaceAll("\\s+$", ""); //right trim
		string = string.replaceAll("^\\s+|\\s+$", ""); //trim beginning and end whitespace
		string = string.replaceAll("\\s+", " ");

		System.out.println("You gave: " + string);
		String textStr[] = string.split("\\s+");

		int arraylength = textStr.length;
/*		
		System.out.println(textStr[0]);
		System.out.println(textStr[1]);

		for(int q =0; q< arraylength; q++){
			System.out.println(textStr[q]);
		}
*/

		//all the arraylists
		ArrayList<Symbol> symbollist= new ArrayList<Symbol>();
		int symbollistlength;
		//Hashtable<String,Symbol> symbollist = new Hashtable<String,Symbol>();
		ArrayList<Use> uselist = new ArrayList<Use>();
		ArrayList<ProgramText> prgmtextlist = new ArrayList<ProgramText>();
		ArrayList<String> errorlist = new ArrayList<String>();
		
		int defcount=0; //how many def pairs
		String symname; //symbol name
		int defvalue; //defined value of symbol
		int deftotal=0; //how many defs total for the symbol and def array in 2nd pass
		int usecount=0; //how many use pairs
		int prgmtextcount=0; //how many pgrm text pairs
		int reladdress; //relative address
		int linecount=0; //which line in the module
		int modulesum=0; //running sum for addresses
		int modulecount=0; //how many modules
		int notdone = 0; //index in textStr array
		while(notdone<arraylength){
			if(linecount %3 ==0){
				System.out.println("These are the definitions:");
				defcount = Integer.parseInt(textStr[notdone]);
				System.out.println(defcount);
				notdone++;
				for(int x =0; x<defcount; x++){
					if(textStr[notdone].matches("[A-Za-z0-9]+") && textStr[notdone+1].matches("[0-9]+") ){
						System.out.println(textStr[notdone]);
						System.out.println(textStr[notdone+1]);

						symname = textStr[notdone];
						//update the symbol definition in the textstr[] array
						defvalue = Integer.parseInt(textStr[notdone+1]);
						defvalue+= modulesum;
						textStr[notdone+1]= "" +defvalue;
						System.out.println("This is the new def value: " + textStr[notdone+1]);

						//make a new symbol
						Symbol sym= new Symbol(symname, defvalue);

						//check if symbol already defined
						//if(symbollist.containsKey(symname)){

						//}
						symbollistlength = symbollist.size();
						if(symbollistlength>0){
							//for(int i = 0; i < symbollistlength; i++) {   
							//	System.out.println(symbollist.get(i).getName());
	    					//	if(symbollist.get(i).getName().equals(symname)){
	    					//symbollist.add(sym);

							for( Symbol newsym: symbollist){
								if(newsym.getName().equals(textStr[notdone]) ){
									System.out.println("Error: The variable " +symname+" is multiply defined; first value used.");
									errorlist.add("Error: The variable " +symname+" is multiply defined; first value used.");
								} else{
									symbollist.add(sym);
								}
								
							}
						} else{
							symbollist.add(sym);
						}

						for (Symbol p : symbollist)
    						System.out.println( "these are symbols: "+ p.getName() + p.getDefinition() );

						notdone+=2;

					} //endif
				}//endfor
			}//endif

			deftotal+=defcount; //end of def line, increment running def count
			linecount++; //use line

			if(linecount %3 ==1){
				System.out.println("These are the uses:");
				usecount = Integer.parseInt(textStr[notdone]);
				System.out.println(usecount);
				notdone++;
				for(int x =0; x<usecount; x++){
					if(textStr[notdone].matches("[A-Za-z0-9]+") && textStr[notdone+1].matches("[0-9]+") ){
						System.out.println(textStr[notdone]);
						System.out.println(textStr[notdone+1]);
						notdone+=2;

					} //endif
				}//endfor
			}//endif

			linecount++; //programtext line

			if(linecount %3 ==2){
				System.out.println("these are the prgmtext: ");
				prgmtextcount = Integer.parseInt(textStr[notdone]);
				System.out.println(prgmtextcount);
				notdone++;
				for(int x =0; x<prgmtextcount; x++){
					if(isAlpha(textStr[notdone]) && textStr[notdone+1].matches("[0-9]+") ){
						System.out.print(textStr[notdone]);
						System.out.print(" " + textStr[notdone+1] + " ");
						//update the relative address in the textStr[] array
						if(textStr[notdone].equals("R") ){
							reladdress = Integer.parseInt(textStr[notdone+1]);
							reladdress+= modulesum;
							textStr[notdone+1]= "" +reladdress;
							System.out.println("This is the new rel address: " + textStr[notdone+1] + " ");

						}
						notdone+=2;

					} //endif
				}//endfor
			}//endif

			
			System.out.println();
			System.out.println("This is the end of module " + modulecount);	
			modulecount++; //end of a module, increment the module count
			modulesum+=prgmtextcount; //increment the runnning module sum

			linecount++;//next line, new module

			//notdone = arraylength;
			//System.out.println(textStr[notdone]);
		}//endwhile

		System.out.println( "The total symbol count is: " + deftotal);
		System.out.println( "The total prgm text count is: " + modulesum);

		for(int q =0; q< arraylength; q++){
			System.out.print(textStr[q]+ " ");
		}

		System.out.println();
		
		for (Symbol p : symbollist)
    			System.out.println( "these are symbols: "+ p.getName() + p.getDefinition() ); 

    	for (String s : errorlist)
    			System.out.println(s); 

		
	} //endmain
} //endLinker
