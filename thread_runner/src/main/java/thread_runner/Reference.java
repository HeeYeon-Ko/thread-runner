package thread_runner;

import java.util.concurrent.atomic.AtomicInteger;

public class Reference {
    public static void temp(String[] args) throws InterruptedException {
        ProgressBarManager2 manager = new ProgressBarManager2();

        // 스레드 1: 작업 A
        Thread threadA = new Thread(() -> {
            for (int i = 0; i <= 100; i += 10) {
                manager.update(0, i);
                try { 
                	Thread.sleep((int)(Math.random() * 500)); 
                } catch (InterruptedException e) {
                	
                }
            }
            // 작업 완료 후 순위 출력
            manager.completeTask("Task A");
        });

        // 스레드 2: 작업 B
        Thread threadB = new Thread(() -> {
            for (int i = 0; i <= 100; i += 100) {
                manager.update(1, i);
                try { Thread.sleep((int)(Math.random() * 300)); } catch (InterruptedException e) {}
            }
            // 작업 완료 후 순위 출력
            manager.completeTask("Task B");
        });

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();
        
        
        manager.finish();
    }
}

class ProgressBarManager2 {
    private int progress1 = 0;
    private int progress2 = 0;
    private final AtomicInteger rankCounter = new AtomicInteger(1); // 순위 카운터
    private String rankResult = ""; // 순위 결과 저장용

    public synchronized void update(int line, int value) {
        if (line == 0) progress1 = value;
        else progress2 = value;
        render();
    }

    // 작업 완료 시 호출되는 메서드
    public synchronized void completeTask(String taskName) {
        int currentRank = rankCounter.getAndIncrement();
        // 결과 문자열을 쌓아둠 (render 시 바 아래에 표시하기 위함)
        rankResult += String.format("\n[순위] %d등: %s", currentRank, taskName);
    }

    private void render() {
    	String red = "\u001B[31m";
    	String blue = "\u001B[34m";
    	
    	
        String bar1 = formatBar("Task A", progress1, red);
        String bar2 = formatBar("Task B", progress2, blue);

        // 현재 바 상태 출력 + 그 아래에 저장된 순위 결과들을 붙여서 출력
        System.out.print("\r" + bar1 + "\n" + bar2 + rankResult);

        // 출력한 줄 수만큼 커서를 다시 위로 올려야 함
        // 기본 2줄(바) + 순위가 추가된 만큼(rankCounter - 1) 위로 이동
        int linesToMoveUp = 1 + (rankCounter.get() - 1);
        System.out.print("\u001B[" + linesToMoveUp + "A\r");
        
        System.out.flush();
    }

    private String formatBar(String label, int p, String color) {
    	String reset = "\u001B[0m";
    	
        int width = 20;
        int filled = p * width / 100;
        StringBuilder sb = new StringBuilder(label + ": [");
        for (int i = 0; i < width; i++) {
            if (i < filled) sb.append(color + "■" + reset);
            else sb.append(" ");
        }
        sb.append("] " + p + "%");
        return sb.toString();
    }

    public synchronized void finish() {
    	render();
        // 모든 출력이 끝난 후 바와 순위표 아래로 커서 이동
        int linesToMoveDown = 2 + (rankCounter.get() - 1);
        for(int i=0; i < linesToMoveDown; i++) System.out.println();
        
        System.out.println("✨ 모든 작업이 성공적으로 완료되었습니다!");
        System.out.flush();
    }
}