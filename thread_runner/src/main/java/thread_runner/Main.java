package thread_runner;

import java.io.IOException;
import java.util.List;

public class Main {
	
	public static int playerCount = 8;
	public static List<String> playerNames;

	public static void main(String[] args) throws IOException, InterruptedException {
		ConsoleTerminal terminal = new ConsoleTerminal();
		
		terminal.printInit();
		playerNames = terminal.getPlayerNames();
		
		ProgressBarManager manager = new ProgressBarManager(terminal);
		
		terminal.loading();
		
		Runnable[] tasks = createThreadTask(manager);
		Thread[] players = new Thread[playerCount];
		for (int i=0; i<playerCount; i++) {
			players[i] = new Thread(tasks[i]); 
		}
		
		
		for (int i=0; i<playerCount; i++) {
			players[i].start(); 
		}
		
		for (Thread player : players) {
		    player.join(); // 각 스레드가 종료될 때까지 메인 스레드가 여기서 대기합니다.
		}		
		
		boolean isBlueTeamWin = manager.cleanUpAndgetGameResult();
		terminal.printEnding(isBlueTeamWin);
	}
	
	private static Runnable[] createThreadTask(ProgressBarManager manager) {
		Runnable[] tasks = new Runnable[playerCount];
		int[] spotPercent = new int[] {20, 40, 60, 80};
		
		for (int i=0; i<playerCount; i++) {
			int threadIndex = i; 
	        String name = playerNames.get(i);
	        
			tasks[i] = () -> {
				int progress = 0;
				int goal = 100;
				boolean[] isPassedSpot = new boolean[4];
				
				while (progress <= goal) {			
					manager.update(threadIndex, progress);
					if (progress == goal) break;
					
					// [핵심] spot 지점 도달 시 팀원 기다리기
			        if (progress == spotPercent[0] && !isPassedSpot[0]) {
			            try {	            	
			                manager.update(threadIndex, spotPercent[0]); // 20% 지점에서 잠깐 멈춤을 시각적으로 보여줌
			                manager.waitForTeam(threadIndex, 0);
			            	isPassedSpot[0] = true; 
			            } catch (InterruptedException e) {
			                Thread.currentThread().interrupt();
			            }
			        } else if (progress == spotPercent[1] && !isPassedSpot[1]) {
			        	try {	            	
			                manager.update(threadIndex, spotPercent[1]); // 40% 지점에서 잠깐 멈춤을 시각적으로 보여줌
			                manager.waitForTeam(threadIndex, 1);
			            	isPassedSpot[1] = true; 
			            } catch (InterruptedException e) {
			                Thread.currentThread().interrupt();
			            }
			        } else if (progress == spotPercent[2] && !isPassedSpot[2]) {
			        	try {	            	
			                manager.update(threadIndex, spotPercent[2]); // 60% 지점에서 잠깐 멈춤을 시각적으로 보여줌
			                manager.waitForTeam(threadIndex, 2);
			            	isPassedSpot[2] = true; 
			            } catch (InterruptedException e) {
			                Thread.currentThread().interrupt();
			            }
			        } else if (progress == spotPercent[3] && !isPassedSpot[3]) {
			        	try {	            	
			                manager.update(threadIndex, spotPercent[3]); // 80% 지점에서 잠깐 멈춤을 시각적으로 보여줌
			                manager.waitForTeam(threadIndex, 3);
			            	isPassedSpot[3] = true; 
			            } catch (InterruptedException e) {
			                Thread.currentThread().interrupt();
			            }
			        }
			        
					try { 
						Thread.sleep((int)(Math.random() * 250)); 
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					progress += getNextStep();
					if (progress < 0)
						progress = 0;
				}
				// 작업 완료 후 순위 출력
				manager.completeTask(name, threadIndex);
			};
		}
		return tasks;
	}

	private static int getNextStep() {
		int next = (int)(Math.random()*15);
		if (next < 2) {
			return -1;
		} else {
			return 1;
		}
	}
}
