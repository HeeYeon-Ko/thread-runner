package thread_runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class ConsoleTerminal {
	
	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	String header = "[" + ColorCode.orange + "Thread " + ColorCode.green + "Runner" + ColorCode.reset + "] ";
	
	public void print(String string) {
		System.out.print(string);
	}
	
	public void println() {
		System.out.println();
	}
	
	public void println(String string) {
		System.out.println(string);
	}
	
	public void flush() {
		System.out.flush();
	}

	public void printInit() {
		println(ColorCode.orange + "\n  _______ _                        _ "    + ColorCode.green + "  _____                                 ");
	    println(ColorCode.orange + " |__   __| |                      | |"    + ColorCode.green + " |  __ \\                                ");
	    println(ColorCode.orange + "    | |  | |__  _ __ ___  __ _  __| |"    + ColorCode.green + " | |__) |   _ _ __  _ __   ___ _ __ ");
	    println(ColorCode.orange + "    | |  | '_ \\| '__/ _ \\/ _` |/ _` |"  + ColorCode.green + " |  _  / | | | '_ \\| '_ \\ / _ \\ '__|");
	    println(ColorCode.orange + "    | |  | | | | | |  __/ (_| | (_| |"    + ColorCode.green + " | | \\ \\ |_| | | | | | | |  __/ |   ");
	    println(ColorCode.orange + "    |_|  |_| |_|_|  \\___|\\__,_|\\__,_|" + ColorCode.green + " |_|  \\_\\__,_|_| |_|_| |_|\\___|_|   ");
	    println(ColorCode.reset  + " ============================================================================== ");
	    println(ColorCode.blue +   "            [우리FISA] " + ColorCode.reset + "넷이서 한마음 - 멀티스레드 동기화 레이스 게임");
	    println(" ============================================================================== \n");
		println(header + "〈넷이서 한마음〉 게임에 오신 것을 환영합니다!");
	}
	
	//플레이 인원 입력받기
	public int selectPlayerCount() throws IOException {
	    int[] options = {2, 4, 6, 8};
	    int selectedIdx = 0;
	    
	    while (true) {
	        // 1. 화면 초기화 및 메뉴 출력
	        print("\r" + header +  "플레이 인원 선택 (방향키/탭 이동, 엔터 선택): ");
	        
	        for (int i = 0; i < options.length; i++) {
	            if (i == selectedIdx) {
	                // 선택된 항목 강조 (반전 효과나 볼드)
	                print("\u001B[7m " + options[i] + " \u001B[0m ");
	            } else {
	                print(" " + options[i] + "  ");
	            }
	        }
	        System.out.flush();

	        // 2. 키 입력 받기 (맥/리눅스 iTerm2 기준)
	        // 이 부분은 환경에 따라 jline 같은 라이브러리를 쓰면 더 편하지만, 
	        // 순수 자바라면 아래처럼 'Raw Mode'와 유사한 처리가 필요할 수 있습니다.
	        int key = getInstantKey(); 

	        if (key == 9 || key == 67) { // Tab 또는 오른쪽 화살표
	            selectedIdx = (selectedIdx + 1) % options.length;
	        } else if (key == 68) { // 왼쪽 화살표
	            selectedIdx = (selectedIdx - 1 + options.length) % options.length;
	        } else if (key == 10 || key == 13) { // Enter
	            println("\n" + header + "✔ 선택 완료: " + options[selectedIdx] + "명");
	            println(header);
	            return options[selectedIdx];
	        }
	    }
	}

	public List<String> getPlayerNames(int playerCount) throws IOException {
		println(header + "플레이어 이름을 한 명씩 입력해주세요.");
		List<String> playerNames = new ArrayList<>();
		int half = playerCount / 2;
		for (int i=0; i<playerCount; i++) {
			if (i < half) {
				print(header + ColorCode.blue + "[블루팀] " + ColorCode.reset); 
			} else {
				print(header + ColorCode.red  + "[레드팀] " + ColorCode.reset); 
			}
			print((i%half+1) + "번 플레이어 이름: ");
			playerNames.add(br.readLine());
		}
		println(header);
		print(header + "게임을 시작하려면 엔터키를 입력해주세요.");
		br.readLine();
		print("\u001B[1A\r                                                          ");
		return playerNames;
	}
	
	public int getInstantKey() throws IOException {
	    Terminal jlineTerminal = TerminalBuilder.terminal();
	    jlineTerminal.enterRawMode(); // 엔터 없이 입력받는 모드 진입
	    int key = jlineTerminal.reader().read();
	    jlineTerminal.close();
	    return key;
	}

	public void loading() throws InterruptedException {
		print("\r" + header +  "플레이어 로딩 중.");
		Thread.sleep(1500);
		print(".");
		Thread.sleep(1500);
		print(".");
		Thread.sleep(1500);
		
		print("\r" + header +  "                   ");
		print("\r" + header +  "플레이어 로딩 완료!");
		Thread.sleep(1000);
		print("\r" + header +  "                   ");
		print("\r" + header +  "3");
		Thread.sleep(250);
		print(".");
		Thread.sleep(250);
		print(".");
		Thread.sleep(250);
		print(".");
		Thread.sleep(250);
		print(" 2");
		Thread.sleep(250);
		print(".");
		Thread.sleep(250);
		print(".");
		Thread.sleep(250);
		print(".");
		Thread.sleep(250);
		print(" 1");
		Thread.sleep(250);
		print(".");
		Thread.sleep(250);
		print(".");
		Thread.sleep(250);
		print(".");
		Thread.sleep(250);
		
		print("\r" + header +  "                  ");
		print("\r" + header +  "Start!");
		Thread.sleep(1000);
		print("/r");
	}

	public void printEnding(boolean isBlueTeamWin) {
		// TODO Auto-generated method stub
		println(header);
		String winner = isBlueTeamWin 
				? ColorCode.blue + "[블루팀] " + ColorCode.reset 
				: ColorCode.red  + "[레드팀] " + ColorCode.reset;
		println(header + winner + "승리!!");
		println(header);
		println(header + "〈넷이서 한마음〉 게임 종료.");
		flush();
	}

}
