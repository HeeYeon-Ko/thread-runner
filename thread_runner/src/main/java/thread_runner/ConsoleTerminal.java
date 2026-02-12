package thread_runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.Cleaner.Cleanable;
import java.util.ArrayList;
import java.util.List;

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

	public String getLogo() {
        StringBuilder sb = new StringBuilder();
        sb.append(ColorCode.orange + "\n  _______ _                        _ " + ColorCode.green
                + "  _____                                 \n");
        sb.append(ColorCode.orange + " |__   __| |                      | |" + ColorCode.green
                + " |  __ \\                                \n");
        sb.append(ColorCode.orange + "    | |  | |__  _ __ ___  __ _  __| |" + ColorCode.green
                + " | |__) |   _ _ __  _ __   ___ _ __ \n");
        sb.append(ColorCode.orange + "    | |  | '_ \\| '__/ _ \\/ _` |/ _` |" + ColorCode.green
                + " |  _  / | | | '_ \\| '_ \\ / _ \\ '__|\n");
        sb.append(ColorCode.orange + "    | |  | | | | | |  __/ (_| | (_| |" + ColorCode.green
                + " | | \\ \\ |_| | | | | | | |  __/ |   \n");
        sb.append(ColorCode.orange + "    |_|  |_| |_|_|  \\___|\\__,_|\\__,_|" + ColorCode.green
                + " |_|  \\_\\__,_|_| |_|_| |_|\\___|_|   \n");
        sb.append(ColorCode.reset + " ======================================================================== \n");
        sb.append(ColorCode.blue + "        [우리FISA] " + ColorCode.reset + "넷이서 한마음 - 멀티스레드 동기화 레이스 게임\n");
        sb.append(" ======================================================================== \n");
        
        return sb.toString();
    }

    // 기존 메서드는 getLogo()를 출력하도록 변경 (하위 호환성 유지)
    public void printInit() {
        print(getLogo());
        println(header + "<넷이서 한마음> 게임에 오신 것을 환영합니다!");
    }

	public List<String> getPlayerNames() throws IOException {
		println(header + "플레이어 이름을 한 명씩 입력해주세요.");
		List<String> playerNames = new ArrayList<>();
		int half = Main.playerCount / 2;
		for (int i = 0; i < Main.playerCount; i++) {
			if (i < half) {
				print(header + ColorCode.blue + "[블루팀] " + ColorCode.reset);
			} else {
				print(header + ColorCode.red + "[레드팀] " + ColorCode.reset);
			}
			print((i % half + 1) + "번 플레이어 이름: ");
			playerNames.add(br.readLine());
		}
		println(header);
		print(header + "게임을 시작하려면 엔터키를 입력해주세요.");
		br.readLine();
		print("\u001B[1A\r                                                          ");
		return playerNames;
	}

	public void loading() throws InterruptedException {
		print("\r" + header + "플레이어 로딩 중.");
		Thread.sleep(1500);
		print(".");
		Thread.sleep(1500);
		print(".");
		Thread.sleep(1500);

		cleanConsole();
		print("\r" + header + "플레이어 로딩 완료!");
		Thread.sleep(1000);
		cleanConsole();
		print("\r" + header);
		for (int i=0; i<12; i++) {
			if (i%4 == 0) {
				Integer result = 3-(i/4);
				print(result.toString());
			} else {
				print(".");
				if (i%4 == 3) {
					print(" ");
				}
			}
			Thread.sleep(250);
		}
		
		cleanConsole();
		print("\r" + header + "Start!");
		Thread.sleep(1000);
		print("/r");
	}
	
	public void cleanConsole() {
		print("\r" + header + "                      ");
	}

	public void printEnding(boolean isBlueTeamWin) {
		println(header);
		String winner = isBlueTeamWin ? ColorCode.blue + "[블루팀] " + ColorCode.reset
				: ColorCode.red + "[레드팀] " + ColorCode.reset;
		println(header + winner + "승리!!");
		println(header);
		println(header + "<넷이서 한마음> 게임 종료.");
		flush();
	}

}
