package thread_runner;

public class ProgressBarManager {

	private final ConsoleTerminal terminal;
	
	// 플레이어 정보 관련 변수
	private int[] playerProgress;
	private boolean[] isPlayerPressing; 	// 플레이어별 대기 상태 저장
	private int rankCounter = 1; 		// 순위 카운터
	private String rankResult = ""; 	// 순위 결과 출력문 저장용
	
	// 레드팀 관련 변수
	private int[] redTeamAtSpot = {1,2,3,4};
	private final Object redTeamLock = new Object();

	// 블루팀 관련 변수
	private int[] blueTeamAtSpot = {1,2,3,4};	
	private final Object blueTeamLock = new Object();

	// 최종 승리팀
	private Boolean isBlueTeamWin = null;

	// 생성자
	public ProgressBarManager(ConsoleTerminal terminal) {
		this.terminal = terminal;
		playerProgress = new int[getPlayerCount()];
		isPlayerPressing = new boolean[getPlayerCount()];
	}
	
	public void update(int playerIndex, int percent) {
		update(playerIndex, percent, false);
	}

	public synchronized void update(int playerIndex, int percent, boolean isPressing) {
		
		// 1등으로 도착한 팀이 어느 팀인지 결정하는 if문
		if (playerProgress[playerIndex] != 0 && percent == 100) {
			if (isBlueTeamWin == null) {	// 아무도 도착한 사람이 없다면, 내가 속한 팀이 우승
				isBlueTeamWin = isBlueTeam(playerIndex);
			}
		}
		
		playerProgress[playerIndex] = percent;
		isPlayerPressing[playerIndex] = isPressing;
		render();
	}

	private boolean isBlueTeam(int playerIndex) {
		return playerIndex < getPlayerCount() / 2;
	}

	// 작업 완료 시 호출되는 메서드
	public synchronized void completeTask(String threadName, int index) {
		int currentRank = rankCounter++;
		// 결과 문자열을 쌓아둠 (render 시 바 아래에 표시하기 위함)
		rankResult += String.format("\n" + terminal.header + "[순위] %d등: ", currentRank);
		rankResult += isBlueTeam(index)
				? ColorCode.blue + threadName + ColorCode.reset
				: ColorCode.red  + threadName + ColorCode.reset;
	}

	private void render() {
		String[] playerBar = new String[getPlayerCount()];
		for (int i = 0; i < playerBar.length; i++) {
			String color = i*2 >= playerBar.length ? ColorCode.red : ColorCode.blue;
			if (isPlayerPressing[i]) {
				color = ColorCode.lime;	// 대기지점에서 대기중이면 라임색
			}
			if (i == 3)
				playerBar[i] = formatBar(Main.playerNames.get(i), playerProgress[i], color) 
				+ "\n" + terminal.header;
			else
				playerBar[i] = formatBar(Main.playerNames.get(i), playerProgress[i], color);
		}

		// 현재 바 상태 출력 + 그 아래에 저장된 순위 결과들을 붙여서 출력
		StringBuilder sb = new StringBuilder();
		sb.append("\r");
		for (String bar : playerBar) {
			sb.append(terminal.header).append(bar).append("\n");
		}
		sb.deleteCharAt(sb.length() - 1).append(rankResult);
		terminal.print(sb.toString());

		// 출력한 줄 수만큼 커서를 다시 위로 올려야 함
		// 기본 선수 인원수 + 순위가 추가된 만큼(rankCounter - 1) 위로 이동
		int linesToMoveUp = getPlayerCount() + (rankCounter - 1);

		if (linesToMoveUp > 0) {
			terminal.print("\u001B[" + linesToMoveUp + "A\r");
		} else {
			terminal.print("\r");
		}

		terminal.flush();
	}

	private String formatBar(String threadName, int percent, String color) {
		int width = 80;
		int filled = percent * width / 100;
		StringBuilder sb = new StringBuilder(threadName + ": [");
		for (int i = 0; i < width; i++) {
			if (i < filled)
				sb.append(color + "■" + ColorCode.reset);
			else
				sb.append(" ");
		}
		sb.append("] " + percent + "%");
		return sb.toString();
	}
	
	public void waitForTeam(int threadIndex, int spot) throws InterruptedException {
		boolean isBlue = isBlueTeam(threadIndex);
	    Object lock = isBlue ? blueTeamLock : redTeamLock;
	    int[] waitCount = isBlue ? blueTeamAtSpot : redTeamAtSpot;

	    synchronized (lock) {
	        waitCount[spot]--; // 내가 도착했으므로 카운트 감소

	        if (waitCount[spot] > 0) {
	            // 아직 팀원이 더 와야 함 (그냥 대기)
	            update(threadIndex, playerProgress[threadIndex], true); // 라임색 적용
	            lock.wait();
	        } else if (waitCount[spot] == 0) {
	            // 내가 마지막 인원! 문을 여는 주인공 (라임색)
	            update(threadIndex, playerProgress[threadIndex], true); // 라임색 적용
	            
	            // 문 열리는 시간 2초 대기
	            Thread.sleep(2000);
	            lock.notifyAll(); // 대기하던 팀원들 깨우기
	        }
	    }
	}

	public synchronized boolean cleanUpAndgetGameResult() {
		// 1. 마지막 상태를 확실히 그리기 위해 한 번 더 호출
		render();

		// 2. 현재 커서는 \u001B[nA에 의해 맨 윗줄 근처에 가 있습니다.
		// 따라서 전체 출력된 줄 수만큼 '엔터'를 쳐서 아래로 내려가야 합니다.
		// 줄 수 = 플레이어 수 + 순위 결과 줄 수 (rankCounter - 1)
		int totalLinesShown = getPlayerCount() + rankCounter;

		// 3. 넉넉하게 줄을 띄워 메이븐 로그가 침범하지 못하게 합니다.
		for (int i = 0; i < totalLinesShown; i++) {
			terminal.println();
		}
		
		return isBlueTeamWin;
	}

	private int getPlayerCount() {
		return Main.playerCount;
	}
}
