import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

//package linkerlab;

public class Linker{

	public static boolean isAlpha(String name) {
    	return name.matches("[a-zA-Z]+");
	}

	public static void main(String[] args){
		Scanner scan = new Scanner(System.in);
		
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

		//all the arraylists
		ArrayList<Info> wholesymbollist= new ArrayList<Info>();
		int wholesymbollistlength;
		int symbollistlength;
		ArrayList<String> wholeSymUsedInModList = new ArrayList<String>(); //type string b/c can't have ArrayList if int(primitive)
		//ArrayList<Info> uselist = new ArrayList<Info>();
		//ArrayList<Info> prgmtextlist = new ArrayList<Info>();
		ArrayList<Module> moduleslist= new ArrayList<Module>(); 
		ArrayList<String> errorlist = new ArrayList<String>();
		
		int defcount=0; //how many def pairs appear in the module
		String symname; //symbol name
		int defvalue; //defined value of symbol
		int deftotal=0; //how many defs total for the symbol and def array in 2nd pass
		int usecount=0; //how many use pairs
		String usename; //name of symbol being used
		int usevalue; //index value location of where symbol is being used
		int prgmtextcount=0; //how many pgrm text pairs
		int reladdress; //relative address
		String addresstype;
		int address;
		int linecount=0; //which line in the module
		int modulesum=0; //running sum for addresses
		int modulecount=0; //how many modules
		int notdone = 0; //index in textStr array

		//============================================================//
		//						FIRST PASS                            //
		//============================================================//
		while(notdone<arraylength){
			ArrayList<Info> symbollist= new ArrayList<Info>();

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
						Info sym= new Info(symname, defvalue);

						//check if symbol already defined
						//if(symbollist.containsKey(symname)){

						//}
						symbollistlength = symbollist.size();
						if(symbollistlength>0){
							for(int i = 0; i < symbollistlength; i++) {   
							//	System.out.println(symbollist.get(i).getName());
	    						if(symbollist.get(i).getName().equals(symname)){
									System.out.println("Error: The variable " +symname+" is multiply defined; first value used.");
									errorlist.add("Error: The variable " +symname+" is multiply defined; first value used.");
								} else{
									symbollist.add(sym);
									break;
								}
								
							}
						} else{
							symbollist.add(sym);
						}
						//--------------------------------------
						wholesymbollistlength = wholesymbollist.size();
						if(wholesymbollistlength>0){
							for(int i = 0; i < wholesymbollistlength; i++) {   
							//	System.out.println(symbollist.get(i).getName());
	    						if(wholesymbollist.get(i).getName().equals(symname)){
									System.out.println("Error: The variable " +symname+" is multiply defined; first value used.");
									errorlist.add("Error: The variable " +symname+" is multiply defined; first value used.");
								} else{
									//add it to the whole symbol list
									wholesymbollist.add(sym);
									//add which module it was defined in to wholeSymUsedInModList
									wholeSymUsedInModList.add(Integer.toString(modulecount) );

									break;
								}
								
							}
						} else{
							//add it to the whole symbol list
							wholesymbollist.add(sym);
							//add which module it was defined in to wholeSymUsedInModList
									wholeSymUsedInModList.add(Integer.toString(modulecount) );
						}
						//----------------------------------------

						notdone+=2;

					} //endif
				}//endfor
			}//endif

			deftotal+=defcount; //end of def line, increment running def count
			linecount++; //use line
			ArrayList<Info> uselist = new ArrayList<Info>();

			if(linecount %3 ==1){
				System.out.println("These are the uses:");
				usecount = Integer.parseInt(textStr[notdone]);
				System.out.println(usecount);
				notdone++;
				for(int x =0; x<usecount; x++){
					if(textStr[notdone].matches("[A-Za-z0-9]+") && textStr[notdone+1].matches("[0-9]+") ){
						System.out.println(textStr[notdone]);
						System.out.println(textStr[notdone+1]);

						usename= textStr[notdone];
						usevalue= Integer.parseInt(textStr[notdone+1]);

						Info newuse= new Info(usename, usevalue);
						uselist.add(newuse);
						notdone+=2;

					} //endif
				}//endfor
			}//endif

			linecount++; //programtext line
			ArrayList<Info> prgmtextlist= new ArrayList<Info>(); 

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

						addresstype=textStr[notdone];
						address = Integer.parseInt(textStr[notdone+1] );

						Info newprgmtext= new Info(addresstype,address);
						prgmtextlist.add(newprgmtext);

						notdone+=2;

					} //endif
				}//endfor
			}//endif

			Module newmodule = new Module(symbollist, uselist, prgmtextlist);
			moduleslist.add(newmodule);
			
			System.out.println();
			System.out.println("This is the end of module " + modulecount);	
			modulecount++; //end of a module, increment the module count
			modulesum+=prgmtextcount; //increment the runnning module sum

			linecount++;//next line, new module

			//notdone = arraylength;
			//System.out.println(textStr[notdone]);
		}//endwhile of FIRST PASS

		
		System.out.println( "The total symbol count is: " + deftotal);
		System.out.println( "The total prgm text count is: " + modulesum);

/*
		for(int q =0; q< arraylength; q++){
			System.out.print(textStr[q]+ " ");
		}
*/

		//print out symbol table
/*		System.out.println();
		System.out.println( "Symbol Table");
		for (Info p : wholesymbollist){
    			System.out.println( p.getName() +" = " +p.getValue() ); 
		}

		System.out.println();
		for (Info r : uselist){
    			System.out.println( r.getName() +" = " +r.getValue() ); 
		}

		System.out.println();
		for (Info u : prgmtextlist){
    			System.out.println( u.getName() +" = " +u.getValue() ); 
		}
*/		
/*
		//print out memory map
		System.out.println();
		System.out.println("Memory Map");
		for(int b=0; b<modulesum; b++){
			System.out.println( b+ ": ");
		}
*/

    	//=============================================================//
		//						SECOND PASS 						   //
		//=============================================================//
    	int modNum; //number for the for loop
    	int useListSize;
    	int ptListSize;
    	int useNum;
    	String ulSymName; //use list symbol name
    	int addressIndex; //address location that will be used
    	int plAddress; //address in prgmtext list
    	int last3digits;
    	int symDef; // current symbol definition
    	int[] symIsUsedArray = new int[wholesymbollist.size()];

    	//Go through each module in the modulesList and edit the addresses
    	for( modNum=0; modNum < moduleslist.size(); modNum++){
    		useListSize = moduleslist.get(modNum).getUseList().size();
    		ptListSize = moduleslist.get(modNum).getPrgmTextList().size();
    		System.out.println("The size of the uselist in mod " + modNum + " is "+ useListSize);

    		int[] ptIsUsedArray= new int[ptListSize]; 

    		//Go through the uselist to get the symbols names and address index in prgmtext
    		for(useNum=0; useNum < useListSize; useNum++){
    			ulSymName = moduleslist.get(modNum).getUseList().get(useNum).getName();
    			addressIndex = moduleslist.get(modNum).getUseList().get(useNum).getValue();
    			System.out.println( "The symbol for module " +modNum+ " is: " + ulSymName + " = " + addressIndex);

    			plAddress= moduleslist.get(modNum).getPrgmTextList().get(addressIndex).getValue();
    			System.out.println(" This is the address at index "+ addressIndex + " : " + plAddress);

    			last3digits = plAddress%1000;
    			System.out.println("The last 3 digits are: " + last3digits);

    			//If the first address is the end of the chain
    		//	wholesymbollistlength = wholesymbollist.size();
    		//	int wslcounter=0;
    			if(last3digits == 777){
    				//for (Info wsl : wholesymbollist){
    				for(int i=0; i<wholesymbollist.size(); i++){
    				/*	if( wslcounter==wholesymbollistlength-1){ // it was never defined
						//	errorlist.add("Error: X21 is not defined; zero used.");
							plAddress= ( (plAddress/1000)*1000);
							//set the new address
							moduleslist.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);
    					}
					*/
    				//	if(wsl.getName().equals(ulSymName) ){
    				//		symDef= wsl.getValue();
    					if(wholesymbollist.get(i).getName().equals(ulSymName) ){
    						symDef = wholesymbollist.get(i).getValue();
    						System.out.println("The def of " + ulSymName+ " = " + symDef);
    						plAddress= ( (plAddress/1000)*1000)+ symDef;
    						//set the new address
    						moduleslist.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);
    						System.out.println( "So the new address is: " + plAddress);

    						
    						//================================================
    						//	ERROR: I OR A ADDRESS USED AS E
    						//================================================	
    						//check if address type is I
    						if(moduleslist.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("I") ){
    							errorlist.add("Error: I type address on use chain; treated as E type.");
    						}
    						//check if address type is A
    						if(moduleslist.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("A") ){
    							errorlist.add("Error: A type address on use chain; treated as E type.");
    						}

    						//====================================================
    						//	ERROR: E TYPE NOT ON USE CHAIN, TREATED AS I TYPE
    						//====================================================	
    						//populate the ptIsUsedArray with 1 for if the address is used
    						//index will stay 0 if it isn't used
    						ptIsUsedArray[addressIndex]=1;

    						//================================================
    						//	ERROR: SYMBOL WAS DEFINED BUT NEVER USED
    						//================================================
    						//mark that the symbol was used in symIsUsedArray
    						symIsUsedArray[i]=1; 
    					
    						    						
						} //endif
						
    				//	wslcounter++;

					} //endfor
    			} //endif

    			
			
    			//If there is a chain of External address
    			//wslcounter=0;
    			while( last3digits != 777){
    				
    				//================================================
					//	ERROR: POINTER EXCEEDS MODULE SIZE
					//================================================	
    				//Check if last3digits exceed the prgmtextlist size
    				if( last3digits > moduleslist.get(modNum).getPrgmTextList().size() ){
    					errorlist.add("Error: Pointer in use chain exceeds module size; chain terminated.");
    					break;
    				}
    				

    				//Go through the wholesymbollist to find the matching symbol and get the value  				
	   				//for (Info wsl : wholesymbollist){
    				for(int i=0; i<wholesymbollist.size(); i++){

    				/*	if( wslcounter==wholesymbollistlength-1){ // it was never defined
						//	errorlist.add("Error: X21 is not defined; zero used.");
							plAddress= ( (plAddress/1000)*1000);
							//set the new address
							moduleslist.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);
							break;
    					}
					*/
    				//	if(wsl.getName().equals(ulSymName) ){
    				//		symDef= wsl.getValue();
    					if(wholesymbollist.get(i).getName().equals(ulSymName) ){
    						symDef = wholesymbollist.get(i).getValue();
    						System.out.println("The def of " + ulSymName+ " = " + symDef);
    						plAddress= ( (plAddress/1000)*1000)+ symDef;
    						//set the new address
    						moduleslist.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);
    						System.out.println( "So the new address is: " + plAddress);

    						//================================================
    						//	ERROR: I OR A ADDRESS USED AS E
    						//================================================	
    						//check if address type is I
    						if(moduleslist.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("I") ){
    							errorlist.add("Error: I type address on use chain; treated as E type.");
    						}
    						//check if address type is A
    						if(moduleslist.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("A") ){
    							errorlist.add("Error: A type address on use chain; treated as E type.");
    						}

    						//====================================================
    						//	ERROR: E TYPE NOT ON USE CHAIN, TREATED AS I TYPE
    						//====================================================	
    						//populate the isUsedArray with 1 for if the address is used
    						//index will stay 0 if it isn't used
    						ptIsUsedArray[addressIndex]=1;

    						//================================================
    						//	ERROR: SYMBOL WAS DEFINED BUT NEVER USED
    						//================================================
    						//mark that the symbol was used in symIsUsedArray
    						symIsUsedArray[i]=1; 

		    				//Go to the index in the prgmtextlist given by last3digits
		    				addressIndex = last3digits;

		    				plAddress= moduleslist.get(modNum).getPrgmTextList().get(addressIndex).getValue();
		    				System.out.println(" This is the address at index "+ addressIndex + " : " + plAddress);
		    				
		    				//Update the last3digits
		    				last3digits = plAddress%1000;
		    				System.out.println("The last 3 digits are: " + last3digits);
		    				plAddress= ( (plAddress/1000)*1000)+ symDef;
		    				//set the new address
		    				moduleslist.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);
		    				System.out.println( "So the new address is: " + plAddress);

		    				//====================================================
    						//	ERROR: E TYPE NOT ON USE CHAIN, TREATED AS I TYPE
    						//====================================================	
    						//populate the ptIsUsedArray with 1 for if the address is used
    						//index will stay 0 if it isn't used
    						ptIsUsedArray[addressIndex]=1;

    						//================================================
    						//	ERROR: SYMBOL WAS DEFINED BUT NEVER USED
    						//================================================
    						//mark that the symbol was used in symIsUsedArray
    						symIsUsedArray[i]=1; 

						} //endif
						
						
    				//	wslcounter++;
    		
					} //endfor 
					
					

    			} //endwhile last3digits != 777
    		}//endfor uselist

    		//====================================================
			//	ERROR: E TYPE NOT ON USE CHAIN, TREATED AS I TYPE
			//====================================================	
			//check the isUsedArray for any 0's and see if it is type E in the prgmTextList
			for(int j=0; j<ptListSize; j++){
				System.out.println("This ptIsUsed is " + ptIsUsedArray[j]+ " at index "+ j);
				if(ptIsUsedArray[j]==0 && moduleslist.get(modNum).getPrgmTextList().get(j).getName().equals("E") ){
					//add to errorlist
					errorlist.add("Error: E type address not on use chain; treated as I type.");
				}
			}

     	} //endfor moduleslist
     	//================================================
		//	ERROR: SYMBOL WAS DEFINED BUT NEVER USED
		//================================================
		//need to check after all the modules are done
		System.out.println("The size of the whole symbol list is " + wholesymbollist.size());
     	for(int j=0; j<wholesymbollist.size(); j++){
			System.out.println("This symbolIsUsedis " + symIsUsedArray[j]+ " at index "+ j);
			if(symIsUsedArray[j]==0){
				//add to errorlist
				errorlist.add("Warning: "+ wholesymbollist.get(j).getName() + "  was defined in module " + wholeSymUsedInModList.get(j) + " but was never used.");
				//System.out.println( wholeSymUsedInModList.size());

			}
		}

     	//------------------------------------------------------------
     	//print out the symbol table and addresses

     	//print out symbol table
		System.out.println();
		System.out.println( "Symbol Table");
		for (Info p : wholesymbollist){
    			System.out.println( p.getName() +" = " +p.getValue() ); 
		}

		System.out.println();
		System.out.println("Memory Map");
		int counter=0;
		int plSize;
		int plNum;

		for( modNum=0; modNum < moduleslist.size(); modNum++){
			plSize = moduleslist.get(modNum).getPrgmTextList().size();
			//System.out.println("The size of the pllist in mod " + modNum + " is "+ plSize);


			//Go through the pl
			for(plNum=0; plNum < plSize; plNum++, counter++){
				System.out.println( counter +" :  " + moduleslist.get(modNum).getPrgmTextList().get(plNum).getValue() );
			}	
		}

		System.out.println();
		for (String s : errorlist){
    			System.out.println(s); 
    	}

		

		
	} //endmain
} //endLinker
