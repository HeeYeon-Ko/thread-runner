package thread_runner;

import java.io.IOException;
import java.util.List;

public class Main {
	
	public static List<String> playerNames;
	static String header = "[" + ColorCode.orange + "Thread " + ColorCode.green + "Runner" + ColorCode.reset + "] ";

	public static void main(String[] args) throws IOException, InterruptedException {
		ConsoleTerminal terminal = new ConsoleTerminal();
		
		terminal.printInit();
		int playerCount = terminal.selectPlayerCount();
		playerNames = terminal.getPlayerNames(playerCount);
		
		ProgressBarManager manager = new ProgressBarManager(terminal, playerCount);
		
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
		Runnable[] tasks = new Runnable[playerNames.size()];
		
		for (int i=0; i<playerNames.size(); i++) {
			int threadIndex = i; 
	        String name = playerNames.get(i);
	        
			tasks[i] = () -> {
				int progress = 0;
				boolean WaitedFirst = false;
				boolean WaitedSecond = false;
				boolean WaitedThird = false;
				
				while (progress <= 100) {			
					manager.update(threadIndex, progress, false);
					if (progress == 100) break;
					
					// [핵심] 50% 도달 시 팀원 기다리기
			        if (progress >= 25 && !WaitedFirst) {
			            try {
			                manager.update(threadIndex, 25, true); // 50% 지점에서 잠깐 멈춤을 시각적으로 보여줌
			                manager.waitForTeam(threadIndex, 0);
			                WaitedFirst = true; 
			            } catch (InterruptedException e) {
			                Thread.currentThread().interrupt();
			            }
			        } else if (progress >= 50 && !WaitedSecond) {
			            try {
			                manager.update(threadIndex, 50, true); // 50% 지점에서 잠깐 멈춤을 시각적으로 보여줌
			                manager.waitForTeam(threadIndex, 1);
			                WaitedSecond = true; 
			            } catch (InterruptedException e) {
			                Thread.currentThread().interrupt();
			            }
			        } else if (progress >= 75 && !WaitedThird) {
			            try {
			                manager.update(threadIndex, 75, true); // 50% 지점에서 잠깐 멈춤을 시각적으로 보여줌
			                manager.waitForTeam(threadIndex, 2);
			                WaitedThird = true; 
			            } catch (InterruptedException e) {
			                Thread.currentThread().interrupt();
			            }
			        } 
			        
					try { 
						Thread.sleep((int)(Math.random() * 250)); 
					} catch (InterruptedException e) {
                	
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
