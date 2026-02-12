package thread_runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

	public void printInit() {
		println(ColorCode.orange + "\n  _______ _                        _ " + ColorCode.green
				+ "  _____                                 ");
		println(ColorCode.orange + " |__   __| |                      | |" + ColorCode.green
				+ " |  __ \\                                ");
		println(ColorCode.orange + "    | |  | |__  _ __ ___  __ _  __| |" + ColorCode.green
				+ " | |__) |   _ _ __  _ __   ___ _ __ ");
		println(ColorCode.orange + "    | |  | '_ \\| '__/ _ \\/ _` |/ _` |" + ColorCode.green
				+ " |  _  / | | | '_ \\| '_ \\ / _ \\ '__|");
		println(ColorCode.orange + "    | |  | | | | | |  __/ (_| | (_| |" + ColorCode.green
				+ " | | \\ \\ |_| | | | | | | |  __/ |   ");
		println(ColorCode.orange + "    |_|  |_| |_|_|  \\___|\\__,_|\\__,_|" + ColorCode.green
				+ " |_|  \\_\\__,_|_| |_|_| |_|\\___|_|   ");
		println(ColorCode.reset + " ============================================================================== ");
		println(ColorCode.blue + "            [우리FISA] " + ColorCode.reset + "넷이서 한마음 - 멀티스레드 동기화 레이스 게임");
		println(" ============================================================================== \n");
		println(header + "〈넷이서 한마음〉 게임에 오신 것을 환영합니다!");
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

		print("\r" + header + "                   ");
		print("\r" + header + "플레이어 로딩 완료!");
		Thread.sleep(1000);
		print("\r" + header + "                   ");
		print("\r" + header + "3");
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

		print("\r" + header + "                  ");
		print("\r" + header + "Start!");
		Thread.sleep(1000);
		print("/r");
	}

	public void printEnding(boolean isBlueTeamWin) {
		println(header);
		String winner = isBlueTeamWin ? ColorCode.blue + "[블루팀] " + ColorCode.reset
				: ColorCode.red + "[레드팀] " + ColorCode.reset;
		println(header + winner + "승리!!");
		println(header);
		println(header + "〈넷이서 한마음〉 게임 종료.");
		flush();
	}

}
