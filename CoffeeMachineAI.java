import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javax.sound.sampled.*;

public class CoffeeMachineAI {
    static final String STT_SECRET = "a90449261cea4f1c896f3d75cd862acf";
    static final String STT_URL = "https://clovaspeech-gw.ncloud.com/recog/v1/stt?lang=Kor";
    static final ExecutorService executor = Executors.newFixedThreadPool(4);
    static final Logger logger = Logger.getLogger(CoffeeMachineAI.class.getName());
    static int totalPrice = 0;

    static {
        try {
            FileHandler fileHandler = new FileHandler("coffeemachine.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void speak(String text) {
        System.out.println(text);
    }

    static CompletableFuture<File> recordAudio() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String path = System.getProperty("user.dir") + "/input.wav";
                File audioFile = new File(path);

                AudioFormat format = new AudioFormat(16000, 16, 1, true, false);
                TargetDataLine line = (TargetDataLine) AudioSystem
                        .getLine(new DataLine.Info(TargetDataLine.class, format));
                line.open(format);
                line.start();

                speak("말씀하세요. 5초간 녹음합니다.");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];

                long start = System.currentTimeMillis();
                while (System.currentTimeMillis() - start < 5000) {
                    int len = line.read(buffer, 0, buffer.length);
                    out.write(buffer, 0, len);
                }
                line.stop();
                line.close();

                AudioInputStream audioStream = new AudioInputStream(
                        new ByteArrayInputStream(out.toByteArray()), format, out.size() / format.getFrameSize());
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, audioFile);

                System.out.println("✅ input.wav 생성됨: " + audioFile.getAbsolutePath());
                return audioFile;
            } catch (Exception e) {
                logger.warning("녹음 실패: " + e.getMessage());
                return null;
            }
        }, executor);
    }

    static CompletableFuture<String> transcribeAudio(File file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(STT_URL))
                        .header("X-CLOVASPEECH-API-KEY", STT_SECRET)
                        .header("Content-Type", "application/octet-stream")
                        .POST(HttpRequest.BodyPublishers.ofFile(file.toPath()))
                        .build();

                HttpResponse<String> response = HttpClient.newHttpClient().send(request,
                        HttpResponse.BodyHandlers.ofString());

                // System.out.println("📦 STT 응답 상태: " + response.statusCode());
                // System.out.println("📨 응답 본문:\n" + response.body());

                if (response.statusCode() == 200 && response.body().contains("\"text\":\"")) {
                    return response.body().split("\"text\":\"")[1].split("\"")[0];
                } else {
                    logger.warning("STT 오류: " + response.body());
                    return "";
                }
            } catch (Exception e) {
                logger.warning("STT 실패: " + e.getMessage());
                return "";
            }
        }, executor);
    }

    // [coffeeChoice, quantity]로 구성된 배열을 반환하며, 그렇지 않은 경우에는 해당 요소를 -1로 설정합니다.
    public static Object[] getVoiceInputWithCoffeeAndQuantity(int min, int max) {
        File file = recordAudio().join();
        if (file == null)
            return getVoiceInputWithCoffeeAndQuantity(min, max);
        String result = transcribeAudio(file).join();
        System.out.println("🎧 인식된 텍스트: " + result);
        // 커피 이름과 수량이 포함되어 있는지 확인합니다.
        String[] namesKorean = { "아메리카노", "연유커피", "베트남 연유커피", "카푸치노", "에스프레소", "카라멜 마키아또", "콜드브루", "모카", "라떼" };
        int coffeeChoice = -1;
        for (int i = 0; i < namesKorean.length; i++) {
            if (result.contains(namesKorean[i])) {
                coffeeChoice = i + 1;
                break;
            }
        }
        int quantity = -1;
        // 문장에서 수량을 찾습니다.
        String digits = result.replaceAll("[^0-9]", "");
        if (!digits.isEmpty()) {
            int number = Integer.parseInt(digits);
            if (number >= min && number <= max)
                quantity = number;
        }
        if (quantity == -1) {
            // 한국어 스타일로 수량을 찾습니다.
            Map<String, Integer> numberMap = Map.of(
                    "한잔", 1, "두잔", 2, "세잔", 3, "네잔", 4, "다섯잔", 5, "여섯잔", 6, "일곱잔", 7, "여덟잔", 8, "아홉잔", 9, "열잔", 10);
            for (Map.Entry<String, Integer> entry : numberMap.entrySet()) {
                if (result.contains(entry.getKey())) {
                    quantity = entry.getValue();
                    break;
                }
            }
        }
        return new Object[] { coffeeChoice, quantity, result == null ? "" : result };
    }

    public static int getVoiceInput(int min, int max) {
        File file = recordAudio().join();
        if (file == null)
            return getVoiceInput(min, max);
        String result = transcribeAudio(file).join();
        System.out.println("🎧 인식된 텍스트: " + result);
        // 문자열에 수량이 포함되어 있는지 확인합니다.
        String digits = result.replaceAll("[^0-9]", "");
        if (!digits.isEmpty()) {
            int number = Integer.parseInt(digits);
            if (number >= min && number <= max)
                return number;
        }
        return parseVoiceInput(result, min, max);
    }

    static int parseVoiceInput(String input, int min, int max) {
        // 한국어 또는 숫자로 된 수량을 찾습니다.
        Map<String, Integer> numberMap = Map.of(
                "한잔", 1, "두잔", 2, "세잔", 3, "네잔", 4, "다섯잔", 5, "여섯잔", 6, "일곱잔", 7, "여덟잔", 8, "아홉잔", 9, "열잔", 10);

        for (Map.Entry<String, Integer> entry : numberMap.entrySet()) {
            if (input.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        // 폴백: 문자열에서 숫자를 추출합니다.
        input = input.replaceAll("[^0-9]", "");
        int num = input.isEmpty() ? -1 : Integer.parseInt(input);
        return (num >= min && num <= max) ? num : getVoiceInput(min, max);
    }

    public void showMainMenu() {
        System.out.println("====== 커피 자판기 메뉴 ======");
        System.out.println("1. 매장에서 마시기");
        System.out.println("2. 포장하기");
        System.out.println("3. 주문 및 결제 안내");
        System.out.println("4. 직원 호출 (문제 발생 시)");
        System.out.println("5. 종료");
        System.out.println("====================================");
        System.out.print("원하는 메뉴 번호를 음성으로 말씀해주세요 (1~5): ");
    }

    public void orderCoffee() {
        totalPrice = 0;
        Map<String, Integer> orderList = new HashMap<>();
        String[] names = { "아메리카노", "연유커피", "베트남 연유커피", "카푸치노", "에스프레소", "카라멜 마키아또", "콜드브루", "모카", "라떼" };
        int[] prices = { 20000, 25000, 30000, 22000, 18000, 27000, 23000, 26000, 24000 };

        while (true) {
            System.out.println();
            System.out.println("🧋 커피 메뉴:");
            for (int i = 0; i < names.length; i++) {
                System.out.printf("%d. %s - %,d원\n", i + 1, names[i], prices[i]);
            }
            System.out.println("10. 주문 종료");
            System.out.print("커피 종류를 음성으로 말씀해주세요 (1~10): ");

            // 음성 인식 결과에서 커피 종류와 수량(있는 경우)을 가져옵니다.
            Object[] coffeeAndQty = getVoiceInputWithCoffeeAndQuantity(1, 10);
            int coffeeChoice = (int) coffeeAndQty[0];
            int quantity = (int) coffeeAndQty[1];
            String lastResult = (String) coffeeAndQty[2];
            // 커피 종류를 아직 확인하지 못했다면 이전 방식으로 다시 질문합니다.
            if (coffeeChoice == -1) {
                coffeeChoice = getVoiceInput(1, 10);
            }
            // 사용자가 종료를 선택한 경우
            if (coffeeChoice == 10)
                break;

            // 수량을 아직 확인하지 못했다면 다시 질문합니다.
            if (quantity == -1) {
                System.out.print("수량을 음성으로 말씀해주세요 (1~10): ");
                quantity = getVoiceInput(1, 10);
            }

            String name = names[coffeeChoice - 1];
            int itemTotal = prices[coffeeChoice - 1] * quantity;
            totalPrice += itemTotal;
            orderList.put(name, orderList.getOrDefault(name, 0) + quantity);

            // 주문 후 바로 선택한 정보를 보여줍니다.
            System.out.printf("\n\n🎯 선택된 커피: %s, 수량: %d잔, 합계: %,d원\n", name, quantity, itemTotal);

            System.out.printf("☕ %s %d잔 추가됨. 소계: %,d원\n", name, quantity, itemTotal);

            System.out.println("\n\n📦 현재까지 주문한 목록:");
            for (Map.Entry<String, Integer> entry : orderList.entrySet()) {
                System.out.printf("- %s: %d잔\n", entry.getKey(), entry.getValue());
            }
            System.out.printf("🧾 총 결제 금액: %,d원\n\n", totalPrice);
        }

        choosePaymentMethod();
    }

    public void choosePaymentMethod() {
        System.out.println("\n\n💳 결제 방법 선택:");
        System.out.println("1. 현금");
        System.out.println("2. 카드 (ATM/Visa 등)");
        System.out.print("결제 방법을 음성으로 말씀해주세요 (1~2): ");
        int method = getVoiceInput(1, 2);
        System.out.println((method == 1 ? "현금" : "카드") + " 결제를 선택하셨습니다.");
        System.out.println("결제가 완료되었습니다. 감사합니다!\n\n");
    }

    public void showPaymentGuide() {
        System.out.println("\n\n📋 주문 및 결제 안내:");
        System.out.println("1. 커피를 선택하고 수량을 입력합니다.");
        System.out.println("2. 총 금액이 표시됩니다.");
        System.out.println("3. 결제 방법을 선택하여 결제를 완료합니다.\n\n");
    }

    public void callStaff() {
        System.out.println("\n\n🆘 문제가 발생하여 직원을 호출합니다. 잠시만 기다려 주세요.\n\n");
    }
}
