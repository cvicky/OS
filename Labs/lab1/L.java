import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;

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

		//System.out.println("You gave: " + string);
		String textStr[] = string.split("\\s+");

		int arrayLength = textStr.length;

		//all the arraylists
		ArrayList<Info> wholeSymbolList= new ArrayList<Info>();
		int wholeSymbolListLength;
		int symbolListLength;
		ArrayList<String> wholeSymDefinedInModList = new ArrayList<String>(); //type string b/c can't have ArrayList if int(primitive)
		ArrayList<String> moduleRunningSum = new ArrayList<String>(); //type string b/c can't have ArrayList if int(primitive)
		ArrayList<String> originalSymDefValueList = new ArrayList<String>(); //type string b/c can't have ArrayList if int(primitive)
		ArrayList<Module> modulesList= new ArrayList<Module>(); 
		ArrayList<String> errorList = new ArrayList<String>();
		
		int defCount=0; //how many def pairs appear in the module
		String symName; //symbol name
		int defValue; //defined value of symbol
		int originalDefValue;
		int defTotal=0; //how many defs total for the symbol and def array in 2nd pass
		int useCount=0; //how many use pairs
		String useName; //name of symbol being used
		int useValue; //index value location of where symbol is being used
		int prgmTextCount=0; //how many pgrm text pairs
		int relAddress; //relative address
		String addressType;
		int address;
		int lineCount=0; //which line in the module
		int moduleSum=0; //running sum for addresses
		moduleRunningSum.add(Integer.toString(moduleSum) ); //add the first value of modulesum since it is initialized
		int moduleCount=0; //how many modules
		int notDone = 0; //index in textStr array

		//============================================================//
		//						FIRST PASS                            //
		//============================================================//
		while(notDone<arrayLength){
			ArrayList<Info> symbolList= new ArrayList<Info>();

			if(lineCount %3 ==0){
				defCount = Integer.parseInt(textStr[notDone]);
				notDone++;
				for(int x =0; x<defCount; x++){
					if(textStr[notDone].matches("[A-Za-z0-9]+") && textStr[notDone+1].matches("[0-9]+") ){

						symName = textStr[notDone];
						//update the symbol definition in the textstr[] array
						defValue = Integer.parseInt(textStr[notDone+1]);
						originalDefValue = defValue;
						//this is the new def value
						defValue+= moduleSum;

						//make a new symbol
						Info sym= new Info(symName, defValue);

						//}
						symbolListLength = symbolList.size();
						if(symbolListLength>0){
							for(int i = 0; i < symbolListLength; i++) {   
								//check for synName in symbolList
	    						if(symbolList.get(i).getName().equals(symName)){
									errorList.add("Error: The variable " +symName+" is multiply defined; first value used.");
								} else{
									symbolList.add(sym);
									break;
								}
								
							}
						} else{ //if there's nothing in the list yet
							symbolList.add(sym);
						}
						//--------------------------------------
						//whole list makes it easier for error checking
						wholeSymbolListLength = wholeSymbolList.size();
						if(wholeSymbolListLength>0){
							for(int i = 0; i < wholeSymbolListLength; i++) {   
	    						if(wholeSymbolList.get(i).getName().equals(symName)){
									errorList.add("Error: The variable " +symName+" is multiply defined; first value used.");
								} else{
									//add it to the whole symbol list
									wholeSymbolList.add(sym);
									//add which module it was defined in to wholeSymDefinedInModList
									wholeSymDefinedInModList.add(Integer.toString(moduleCount) );
									//add the original sym def value to the originalSymDefValueList
									originalSymDefValueList.add(Integer.toString(originalDefValue) );

									break;
								}
							}
						} else{//if there's nothing in the list yet
							//add it to the whole symbol list
							wholeSymbolList.add(sym);
							//add which module it was defined in to wholeSymDefinedInModList
							wholeSymDefinedInModList.add(Integer.toString(moduleCount) );
							//add the original sym def value to the originalSymDefValueList
							originalSymDefValueList.add(Integer.toString(originalDefValue) );
						}

						notDone+=2;

					} //endif
				}//endfor
			}//endif

			defTotal+=defCount; //end of def line, increment running def count
			lineCount++; //use line
			ArrayList<Info> useList = new ArrayList<Info>();

			if(lineCount %3 ==1){
				useCount = Integer.parseInt(textStr[notDone]);
				notDone++;
				for(int x =0; x<useCount; x++){
					if(textStr[notDone].matches("[A-Za-z0-9]+") && textStr[notDone+1].matches("[0-9]+") ){
						useName= textStr[notDone];
						useValue= Integer.parseInt(textStr[notDone+1]);

						//create a newUse object
						Info newUse= new Info(useName, useValue);
						useList.add(newUse);
						notDone+=2;

					} //endif
				}//endfor
			}//endif

			lineCount++; //programtext line
			ArrayList<Info> prgmTextList= new ArrayList<Info>(); 

			if(lineCount %3 ==2){
				prgmTextCount = Integer.parseInt(textStr[notDone]);
				notDone++;
				for(int x =0; x<prgmTextCount; x++){
					if(isAlpha(textStr[notDone]) && textStr[notDone+1].matches("[0-9]+") ){
						//update the relative address in the textStr[] array
						if(textStr[notDone].equals("R") ){
							relAddress = Integer.parseInt(textStr[notDone+1]);
							relAddress+= moduleSum;
							textStr[notDone+1]= "" +relAddress;
						}//endif it's a relative address
						addressType=textStr[notDone];
						address = Integer.parseInt(textStr[notDone+1] );

						Info newPrgmText= new Info(addressType,address);
						prgmTextList.add(newPrgmText);

						notDone+=2;

					} //endif
				}//endfor
			}//endif

			Module newModule = new Module(symbolList, useList, prgmTextList);
			modulesList.add(newModule);
			
			moduleCount++; //end of a module, increment the module count
			moduleSum+=prgmTextCount; //increment the runnning module sum
			moduleRunningSum.add(Integer.toString(moduleSum) ); //add the runningsum for the current module
			lineCount++;//next line, new module

		}//endwhile of FIRST PASS

		//==========================================================
		//	ERROR: VALUE OF SYMBOL IS OUTSIDE OF MODULE, ZERO USED
		//==========================================================

		int definedInMod;	
		int moduleSize;

	  	//Iterate through the symbol original value list
		for(int i=0; i<originalSymDefValueList.size(); i++){
			originalDefValue = Integer.parseInt(originalSymDefValueList.get(i) );

			//check which module the symbol was defined in
			definedInMod =Integer.parseInt(wholeSymDefinedInModList.get(i));
		
			//get the size of the module it was defined in
			moduleSize = modulesList.get(definedInMod).getPrgmTextList().size();
		
			//check if the originalDefValue is larger than the module size -1 b/c these are index values starting from 0
			if(originalDefValue > moduleSize-1){
				wholeSymbolList.get(i).setValue( Integer.parseInt( moduleRunningSum.get(definedInMod) ) ); //parse b/c moduleRunningSum is String values
				errorList.add("Error: The value of " + wholeSymbolList.get(i).getName() + " is outside module " + definedInMod + "; zero (relative) used.");
			
			}
		
				
		}

    	//=============================================================//
		//						SECOND PASS 						   //
		//=============================================================//
    	int modNum; //number for the for loop
    	int useListSize;
    	int ptListSize;
    	int useNum;
    	int found;
    	String ulSymName; //use list symbol name
    	int addressIndex; //address location that will be used
    	int plAddress; //address in prgmtext list
    	int last3digits;
    	int symDef; // current symbol definition
    	int[] symIsUsedArray = new int[wholeSymbolList.size()];

    	//Go through each module in the modulesList and edit the addresses
    	for( modNum=0; modNum < modulesList.size(); modNum++){
    		useListSize = modulesList.get(modNum).getUseList().size();
    		ptListSize = modulesList.get(modNum).getPrgmTextList().size();

    		int[] ptIsUsedArray= new int[ptListSize]; 

    		//Go through the uselist to get the symbols names and address index in prgmtext
    		for(useNum=0; useNum < useListSize; useNum++){
    			found =0;
    			ulSymName = modulesList.get(modNum).getUseList().get(useNum).getName();
    			addressIndex = modulesList.get(modNum).getUseList().get(useNum).getValue();
    		
    			plAddress= modulesList.get(modNum).getPrgmTextList().get(addressIndex).getValue();

    			last3digits = plAddress%1000;

    			//If the first address is the end of the chain
    			wholeSymbolListLength = wholeSymbolList.size();
  
    			if(last3digits == 777){
    				found=0; //0 means symbol is not found in sybmol list
    				for(int i=0; i<wholeSymbolListLength; i++){
    					
    					if(wholeSymbolList.get(i).getName().equals(ulSymName) ){
    						found=1; //symbol was defined in symbol list
    						symDef = wholeSymbolList.get(i).getValue();
    						plAddress= ( (plAddress/1000)*1000)+ symDef;
    						//set the new address
    						modulesList.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);
    						
    						//================================================
    						//	ERROR: I OR A ADDRESS USED AS E
    						//================================================	
    						//check if address type is I
    						if(modulesList.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("I") ){
    							errorList.add("Error: I type address on use chain; treated as E type.");
    						}
    						//check if address type is A
    						if(modulesList.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("A") ){
    							errorList.add("Error: A type address on use chain; treated as E type.");
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
    					
    						    						
						} //endif gets a match in symbollist

						//================================================
						//	ERROR: SYMBOL WAS NOT DEFINED, ZERO USED
						//================================================	
						//if it reaches here it wasn't found
						if(i== wholeSymbolList.size()-1 && found ==0){ // you've reached the end of the symbollist, it was never defined
							errorList.add("Error: " + ulSymName+ " is not defined; zero used.");
							plAddress= ( (plAddress/1000)*1000);
							//set the new address
							modulesList.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);

    						//================================================
    						//	ERROR: I OR A ADDRESS USED AS E
    						//================================================	
    						//check if address type is I
    						if(modulesList.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("I") ){
    							errorList.add("Error: I type address on use chain; treated as E type.");
    						}
    						//check if address type is A
    						if(modulesList.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("A") ){
    							errorList.add("Error: A type address on use chain; treated as E type.");
    						}

    						//====================================================
    						//	ERROR: E TYPE NOT ON USE CHAIN, TREATED AS I TYPE
    						//====================================================	
    						//populate the isUsedArray with 1 for if the address is used
    						//index will stay 0 if it isn't used
    						ptIsUsedArray[addressIndex]=1;

						} //endif reached the end of the symbollist b/c it wasn't defined
					} //endfor symbollist
    			} //endif
			
    			//If there is a chain of External address
    			//wslcounter=0;
    			while( last3digits != 777){
    				found =0;
    				//================================================
					//	ERROR: POINTER EXCEEDS MODULE SIZE
					//================================================	
    				//Check if last3digits exceed the prgmtextlist size
    				if( last3digits > modulesList.get(modNum).getPrgmTextList().size() ){
    					errorList.add("Error: Pointer in use chain exceeds module size; chain terminated.");
    					break;
    				}
    				

    				//Go through the wholesymbollist to find the matching symbol and get the value  				
    				for(int i=0; i<wholeSymbolList.size(); i++){
    					if(wholeSymbolList.get(i).getName().equals(ulSymName) ){
    						found =1;
    						symDef = wholeSymbolList.get(i).getValue();
    						plAddress= ( (plAddress/1000)*1000)+ symDef;
    						//set the new address
    						modulesList.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);

    						//================================================
    						//	ERROR: I OR A ADDRESS USED AS E
    						//================================================	
    						//check if address type is I
    						if(modulesList.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("I") ){
    							errorList.add("Error: I type address on use chain; treated as E type.");
    						}
    						//check if address type is A
    						if(modulesList.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("A") ){
    							errorList.add("Error: A type address on use chain; treated as E type.");
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

		    				plAddress= modulesList.get(modNum).getPrgmTextList().get(addressIndex).getValue();
		    			
		    				//Update the last3digits
		    				last3digits = plAddress%1000;
		    				plAddress= ( (plAddress/1000)*1000)+ symDef;
		    				//set the new address
		    				modulesList.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);
		    			
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

						} //endif got a match in symbollist

						//================================================
						//	ERROR: SYMBOL WAS NOT DEFINED, ZERO USED
						//================================================	
						//if it reaches here it wasn't found
						if(i== wholeSymbolList.size()-1 && found ==0){ // you've reached the end of the symbollist, it was never defined

		    				if(last3digits == 777){
			    				errorList.add("Error: " + ulSymName+ " is not defined; zero used.");
			    				plAddress= ( (plAddress/1000)*1000);
			    				//set the new address
			    				modulesList.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);
			    			
			    				//====================================================
	    						//	ERROR: E TYPE NOT ON USE CHAIN, TREATED AS I TYPE
	    						//====================================================	
	    						//populate the ptIsUsedArray with 1 for if the address is used
	    						//index will stay 0 if it isn't used
	    						ptIsUsedArray[addressIndex]=1;

    							break;
    						} //endif last3digits ==777, change the address, break out of loop

							errorList.add("Error: " + ulSymName+ " is not defined; zero used.");
							plAddress= ( (plAddress/1000)*1000);
							//set the new address
							modulesList.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);
							
    						//================================================
    						//	ERROR: I OR A ADDRESS USED AS E
    						//================================================	
    						//check if address type is I
    						if(modulesList.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("I") ){
    							errorList.add("Error: I type address on use chain; treated as E type.");
    						}
    						//check if address type is A
    						if(modulesList.get(modNum).getPrgmTextList().get(addressIndex).getName().equals("A") ){
    							errorList.add("Error: A type address on use chain; treated as E type.");
    						}

    						//====================================================
    						//	ERROR: E TYPE NOT ON USE CHAIN, TREATED AS I TYPE
    						//====================================================	
    						//populate the isUsedArray with 1 for if the address is used
    						//index will stay 0 if it isn't used
    						ptIsUsedArray[addressIndex]=1;

  		    				//Go to the index in the prgmtextlist given by last3digits
		    				addressIndex = last3digits;

 		    				plAddress= modulesList.get(modNum).getPrgmTextList().get(addressIndex).getValue();
		  		    				
		    				//Update the last3digits
		    				last3digits = plAddress%1000;

		    				if(last3digits == 777){
			    				errorList.add("Error: " + ulSymName+ " is not defined; zero used.");
			    				plAddress= ( (plAddress/1000)*1000);
			    				//set the new address
			    				modulesList.get(modNum).getPrgmTextList().get(addressIndex).setValue(plAddress);

			    				//====================================================
	    						//	ERROR: E TYPE NOT ON USE CHAIN, TREATED AS I TYPE
	    						//====================================================	
	    						//populate the ptIsUsedArray with 1 for if the address is used
	    						//index will stay 0 if it isn't used
	    						ptIsUsedArray[addressIndex]=1;

    							break;
    						} //endif last3digits ==777, change the address, break out of loop
						} //endif reached the end of the symbollist b/c symbol was NEVER FOUND    		
					} //endfor 		
    			} //endwhile last3digits != 777
    		}//endfor uselist

    		//====================================================
			//	ERROR: E TYPE NOT ON USE CHAIN, TREATED AS I TYPE
			//====================================================	
			//check the isUsedArray for any 0's and see if it is type E in the prgmTextList
			for(int j=0; j<ptListSize; j++){
				if(ptIsUsedArray[j]==0 && modulesList.get(modNum).getPrgmTextList().get(j).getName().equals("E") ){
					//add to errorlist
					errorList.add("Error: E type address not on use chain; treated as I type.");
				} //endif
			}//endfor

     	} //endfor moduleslist

     	//================================================
		//	ERROR: SYMBOL WAS DEFINED BUT NEVER USED
		//================================================
		//need to check after all the modules are done
     	for(int j=0; j<wholeSymbolList.size(); j++){
			if(symIsUsedArray[j]==0){
				//add to errorlist
				errorList.add("Warning: "+ wholeSymbolList.get(j).getName() + "  was defined in module " + wholeSymDefinedInModList.get(j) + " but never used.");
			} //endif
		}//endfor

     	//------------------------------------------------------------
     	//print out the symbol table and addresses

     	//print out symbol table
		System.out.println();
		System.out.println( "Symbol Table");
		for (Info p : wholeSymbolList){
    			System.out.println( p.getName() +" = " +p.getValue() ); 
		}

		System.out.println();
		System.out.println("Memory Map");
		int counter=0;
		int plSize;
		int plNum;


		for( modNum=0; modNum < modulesList.size(); modNum++){
			plSize = modulesList.get(modNum).getPrgmTextList().size();
			//System.out.println("The size of the pllist in mod " + modNum + " is "+ plSize);

			//Go through the pl
			for(plNum=0; plNum < plSize; counter++, plNum++){
				System.out.println( counter +" :  " + modulesList.get(modNum).getPrgmTextList().get(plNum).getValue() );
			}	
		}

		System.out.println();
		for (String s : errorList){
    			System.out.println(s); 
    	}
    	System.out.println();
		
	} //endmain
} //endLinker
