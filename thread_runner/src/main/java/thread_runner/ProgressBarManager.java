package thread_runner;

import java.util.concurrent.atomic.AtomicBoolean;

public class ProgressBarManager {

    private final ConsoleTerminal terminal;
    
    private int[] playerProgress;
    private int rankCounter = 1;
    private boolean[] isPlayerPressing; 
    private String[] rankDisplay;      
    
    private int[] redTeamAtSpot = {1, 2, 3, 4};
    private final Object redTeamLock = new Object();
    
    private int[] blueTeamAtSpot = {1, 2, 3, 4};
    private final Object blueTeamLock = new Object();
    
    private volatile Boolean isBlueTeamWin = null;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);
    private Thread renderThread;
    
    private int[] lastProgress;
    private final int LOGO_HEIGHT = 10;

    public ProgressBarManager(ConsoleTerminal terminal) {
        this.terminal = terminal;
        this.playerProgress = new int[getPlayerCount()];
        this.lastProgress = new int[getPlayerCount()];
        for(int i=0; i<getPlayerCount(); i++) lastProgress[i] = -1;
        this.isPlayerPressing = new boolean[getPlayerCount()];
        this.rankDisplay = new String[getPlayerCount()];

        // 초기화 시 화면 정리
        terminal.print("\033[2J\033[H\033[?25l");
        for (int i = 0; i < 20; i++) terminal.println("");
        terminal.print("\033[2J\033[H\033[?25l");
        terminal.print(terminal.getLogo());
        startRenderThread();
    }

    private void startRenderThread() {
        renderThread = new Thread(() -> {
            while (isRunning.get()) {
                try {
                    render();
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        renderThread.setDaemon(true); 
        renderThread.start();
    }

	private void render() {
	    for (int i = 0; i < getPlayerCount(); i++) {
	        int currentProgress;
	        boolean pressing;
	        
	        synchronized(this) {
	            currentProgress = playerProgress[i];
	            pressing = isPlayerPressing[i];
	        }
	
	        if (currentProgress != lastProgress[i]) {
	            int row = LOGO_HEIGHT + i + (i >= getPlayerCount() / 2 ? 2 : 1); 
	            terminal.print("\033[" + row + ";1H"); 
	
	            String color = pressing ? ColorCode.lime : (isBlueTeam(i) ? ColorCode.blue : ColorCode.red);
	            String bar = formatBar(Main.playerNames.get(i), currentProgress, color);
	            String rank = (rankDisplay[i] != null) ? rankDisplay[i] : "";
	            
	            terminal.print(bar + rank + "\u001B[K");
	            lastProgress[i] = currentProgress;
	        }
	    }
	    terminal.flush();
	}

    private String formatBar(String name, int percent, String color) {
        int width = 45; 
        int filled = (int) (width * (percent / 100.0));
        
        // 프로그레스바 그리기
        StringBuilder sb = new StringBuilder();
        sb.append(terminal.header);
        sb.append(String.format(" %s: [", name));
        sb.append(color);
        for (int i = 0; i < width; i++) {
            if (i < filled) sb.append("■");
            else sb.append(" ");
        }
        sb.append(ColorCode.reset).append("] ").append(String.format("%3d%%", percent));
        
        return sb.toString();
    }

    public synchronized void update(int playerIndex, int percent) {
        update(playerIndex, percent, false);
    }

    public synchronized void update(int playerIndex, int percent, boolean isPressing) {
    	// 1등으로 들어오는 쓰레드 팀이 승리
        if (playerProgress[playerIndex] != 0 && percent == 100) {
            if (isBlueTeamWin == null) isBlueTeamWin = isBlueTeam(playerIndex);
        }
        playerProgress[playerIndex] = percent;
        isPlayerPressing[playerIndex] = isPressing;
    }

    public synchronized void completeTask(String threadName, int index) {
        int currentRank = rankCounter++;
        String teamColor = isBlueTeam(index) ? ColorCode.blue : ColorCode.red;
        rankDisplay[index] = String.format("  %s%d등%s", teamColor, currentRank, ColorCode.reset);
        
        // 포디움은 메달 추가
        if (currentRank == 1) {
        	rankDisplay[index] += "🥇" ;
        } else if (currentRank == 2) {
        	rankDisplay[index] += "🥈" ;
        } else if (currentRank == 3) {
        	rankDisplay[index] += "🥉" ;
        }
    }

    private boolean isBlueTeam(int playerIndex) {
        return playerIndex < getPlayerCount() / 2;
    }

    private int getPlayerCount() {
        return Main.playerCount;
    }

    public void waitForTeam(int threadIndex, int spot) throws InterruptedException {
        boolean isBlue = isBlueTeam(threadIndex);
        Object lock = isBlue ? blueTeamLock : redTeamLock;
        int[] waitCount = isBlue ? blueTeamAtSpot : redTeamAtSpot;

        synchronized (lock) {
            waitCount[spot]--; 	// 내가 도착했으므로 카운트 감소

            if (waitCount[spot] > 0) {	// 팀원을 기다려야 하는 경우 대기
                update(threadIndex, playerProgress[threadIndex], true);
                lock.wait();
            } else if (waitCount[spot] == 0) { // 문을 열 마지막 팀원인 경우
                update(threadIndex, playerProgress[threadIndex], true);
                Thread.sleep(2000);	// 문 열리는 시간(2초) 대기 후 쓰레드 모두 깨우기
                lock.notifyAll();
            }
        }
    }

    public synchronized boolean cleanUpAndgetGameResult() {
        isRunning.set(false);
        try {
            if (renderThread != null) renderThread.join(100);
        } catch (InterruptedException e) { e.printStackTrace(); }

        render();
        terminal.print("\033[20;1H\033[?25h");
        return isBlueTeamWin != null ? isBlueTeamWin : false;	// 승리팀 정보 리턴
    }
}
