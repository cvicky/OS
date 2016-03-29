import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class LCFS {
	public static void main(String[] args) throws IOException {
		Scheduler LcfsScheduler = new Scheduler();
		Scanner input2 = null;

		// file handling
		if (args[0].equals("--verbose")) {
			LcfsScheduler.verbose = true;
			LcfsScheduler.fileHandler(args[1]);
			input2 = new Scanner(new BufferedReader(new FileReader(args[2])));

		} else {
			LcfsScheduler.fileHandler(args[0]);
			input2 = new Scanner(new BufferedReader(new FileReader(args[1])));

		}
		// process list
		ArrayList<Process> processList = LcfsScheduler.processList;
		LcfsScheduler.arrivalSort(processList);

		Process p = null;
		int time = 0;

		// ready processes
		ArrayList<Process> readyQueue = new ArrayList<Process>();

		LcfsScheduler.verbose(processList, time);

		// while the processes are not all terminated
		while (!LcfsScheduler.isDone(processList)) {

			//DO ARRIVING PROCESSES
			// add processes to queue
			for (int i = 0; i < processList.size(); i++) {
				Process t = processList.get(i);
				if (!readyQueue.contains(t) && t.getArrivalTime() <= time
						&& (t.getStatus() == t.UNSTARTED || t.getStatus() == t.READY)) {
					// processes are ready
					t.setStatus(t.READY);
					readyQueue.add(t);
				}
			}

			//DO READY PROCESSES
			// there are processes that are ready to run
			if (!readyQueue.isEmpty()) {
				// choose ready process to run

				Process u;
				int aT;
				//System.out.println( "This is the arrival time aT: " + aT);
				int index =-1;
				int aT2;

				for (int i = readyQueue.size()-2 ; i> -1; i--) {
					
					u = readyQueue.get(i+1);
					aT = u.getArrivalTime();
				//	System.out.println( "This is the arrival time aT: " + aT);
					aT2 = readyQueue.get(i).getArrivalTime();
				//	System.out.println( "This is the arrival time aT2: " + aT2);
					if ( u.getStatus() ==3 && readyQueue.get(i).getStatus() ==3){
						if( aT2 == aT  ){
							aT= aT2;
						//	System.out.println( "aT now = aT2: " + aT);
							index = i;
						}else if (aT2 < aT ) {
							index = i;
							break;
						}else if( i < i+1){
							index = i;
						}
					}
					
				}
			
				if( index != -1){
						p = readyQueue.get(index);
						readyQueue.remove(index);
				}else{
						//get it from the back
						p = readyQueue.get(readyQueue.size()-1 );
						readyQueue.remove(readyQueue.size()-1 );

				}


				// set its CPU burst and IO burst
				p.setCurrentcpuBurst(LcfsScheduler.randomOS(input2.nextInt(), p.getCpuBurst()));

				// if the cpu burst is larger than the remaining cpu time
				if (p.getCurrentcpuBurst() > p.getRemainingCPUTime()) {
					p.setCurrentcpuBurst(p.getRemainingCPUTime());
				}

				// set status to running
				p.setStatus(p.RUNNING);

				
				// run until burst is over
				while (p.getCurrentcpuBurst() > 0) {
					time++;
					LcfsScheduler.verbose(processList, time);
					LcfsScheduler.addWaitTime(processList);
					boolean processBlocked = false;

					for (int j = 0; j < processList.size(); j++) {
						Process currentPro = processList.get(j);
						// if the status of the current process is blocked
						if (currentPro.getStatus() == currentPro.BLOCKED) {
							processBlocked = true;
							currentPro.setCurIOTime(currentPro.getCurIOTime() - 1);
							currentPro.setTotalioTime(currentPro.getTotalioTime() + 1);

							// incremement io time
							if (currentPro.getCurIOTime() == 0) {
								// set the status to ready if io time is up
								currentPro.setStatus(currentPro.READY);
							}
						}

					}

					//DO BLOCKED PROCESSES
					if (processBlocked) {
						LcfsScheduler.IOUtilization++;
					}

					// add to queue
					for (int i = 0; i < processList.size(); i++) {
						Process t = processList.get(i);
						if (!readyQueue.contains(t) && t.getArrivalTime() <= time
								&& (t.getStatus() == t.UNSTARTED || t.getStatus() == t.READY)) {
							t.setStatus(t.READY);
							readyQueue.add(t);
						}
					}
					// as long as the remaining cpu time is greater than 0
					// continue to decrement
					if (p.getRemainingCPUTime() > 0) {
						p.setCurrentcpuBurst(p.getCurrentcpuBurst() - 1);
						p.setRemainingCPUTime(p.getRemainingCPUTime() - 1);
						if (p.getRemainingCPUTime() == 0) {
							p.setStatus(p.END);
							p.setFinishingTime(time);
							p.setTurnAroundTime(p.getFinishingTime() - p.getArrivalTime());
							break;
						}
					}
				}


				//DO RUNNING PROCESSES
				// if state == running, make it blocked and set the io time
				if (p.getStatus() == p.RUNNING){
					p.setStatus(p.BLOCKED);
					
					// set io time
					p.setCurIOTime(LcfsScheduler.randomOS(input2.nextInt(), p.getIoBurst()));
				}

			} else {
				time++;
				LcfsScheduler.verbose(processList, time);
				LcfsScheduler.addWaitTime(processList);

				boolean processBlocked = false;

				// unblock the process and decrement io time
				for (int i = 0; i < processList.size(); i++) {
					Process t = processList.get(i);

					if (t.getStatus() == t.BLOCKED) {
						processBlocked = true;
						t.setCurIOTime(t.getCurIOTime() - 1);
						t.setTotalioTime(t.getTotalioTime() + 1);

						if (t.getCurIOTime() == 0) {
							t.setStatus(t.READY);
						}
					}
				}
				if (processBlocked) {
					LcfsScheduler.IOUtilization++;
				}
			}

		}
		LcfsScheduler.setFinishingTime(time);
		System.out.println("The scheduling algorithm used was Last Come First Serve\n");
		LcfsScheduler.printProcesses();
		LcfsScheduler.printSummary();

	}

}
