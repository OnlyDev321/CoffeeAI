# ☕ Coffee-AI

## 📚 Project Resources

Additional project materials, reports, and related documents are available in the Google Drive folder below:

🔗 https://drive.google.com/drive/folders/1tJxtbMnliGpu0P3JcQOPgAzw1XG2pHUs?usp=drive_link

The folder contains supporting documents used throughout the development and evaluation of the Coffee-AI project.

---

### AI Voice-Based Coffee Ordering System

> Coffee-AI is an intelligent coffee ordering system developed in Java that enables customers to order coffee entirely through voice interaction. The system records customer speech, converts it into text using NAVER CLOVA Speech Recognition (STT), extracts order information such as coffee type and quantity, and completes the ordering and payment process automatically.

---

## 📌 Project Overview

Traditional kiosk systems require users to interact through touch screens. Coffee-AI introduces a more natural ordering experience by allowing customers to communicate using their voice.

The system supports:

- Voice-based menu navigation
- Automatic coffee recognition
- Quantity extraction from speech
- Real-time order management
- Payment method selection
- Staff assistance requests

This project demonstrates the practical application of Artificial Intelligence technologies in self-service environments.

---

## 🎯 Objectives

- Provide a hands-free coffee ordering experience.
- Improve accessibility for customers.
- Reduce complexity in kiosk interaction.
- Demonstrate the integration of AI speech technologies into Java applications.

---

## ✨ Features

### 🎤 Voice Recognition Ordering

Customers can place orders by speaking naturally.

Example:

> "아메리카노 두잔 주세요"
>
> (Two cups of Americano, please.)

The system automatically recognizes:

- Coffee type
- Quantity

---

### ☕ Coffee Menu Management

Supported coffee menu:

| No  | Menu                                          | Price      |
| --- | --------------------------------------------- | ---------- |
| 1   | Americano (아메리카노)                        | 20,000 KRW |
| 2   | Condensed Milk Coffee (연유커피)              | 25,000 KRW |
| 3   | Vietnamese Condensed Coffee (베트남 연유커피) | 30,000 KRW |
| 4   | Cappuccino (카푸치노)                         | 22,000 KRW |
| 5   | Espresso (에스프레소)                         | 18,000 KRW |
| 6   | Caramel Macchiato (카라멜 마키아또)           | 27,000 KRW |
| 7   | Cold Brew (콜드브루)                          | 23,000 KRW |
| 8   | Mocha (모카)                                  | 26,000 KRW |
| 9   | Latte (라떼)                                  | 24,000 KRW |

---

### 🔍 Automatic Quantity Detection

The system recognizes both numeric and Korean expressions.

Examples:

| Voice Input | Quantity |
| ----------- | -------- |
| 2잔         | 2        |
| 한잔        | 1        |
| 두잔        | 2        |
| 세잔        | 3        |
| 네잔        | 4        |
| 다섯잔      | 5        |

---

### 📦 Order Summary

After each order, the system displays:

- Selected menu
- Ordered quantity
- Subtotal price
- Current order list
- Total payment amount

---

### 💳 Payment Support

Available payment methods:

- Cash
- Card (ATM / Visa)

After payment selection, the system completes the transaction.

---

### 🆘 Staff Assistance

If customers encounter problems, they can request staff assistance through voice commands.

---

## 🏗️ System Architecture

text Customer Voice ↓ Audio Recording (Java Sound API) ↓ WAV File Generation (input.wav) ↓ NAVER CLOVA Speech STT ↓ Speech-to-Text Conversion ↓ Coffee & Quantity Extraction ↓ Order Processing ↓ Payment Selection ↓ Order Completion

---

## 🛠️ Technology Stack

### Programming Language

- Java

### Java Version

- Java 17+

### Speech Recognition

- NAVER CLOVA Speech Recognition API

### Audio Processing

- Java Sound API
- AudioInputStream
- TargetDataLine

### Networking

- Java HttpClient

### Concurrency

- CompletableFuture
- ExecutorService

### Logging

- java.util.logging
- FileHandler

---

## 📂 Project Structure

```text
Coffee-AI
├── Main.java
├── CoffeeMachineAI.java
├── input.wav
├── coffeemachine.log
└── README.md
```

---

## 🔄 Ordering Process

### 1. Main Menu

The customer selects:

1. Dine-in
2. Take-out
3. Payment Guide
4. Call Staff
5. Exit

---

### 2. Voice Ordering

Example:

text "베트남 연유커피 두잔 주세요"

System output:

text Selected Coffee: 베트남 연유커피 Quantity: 2

---

### 3. Order Confirmation

Example:

text ☕ 베트남 연유커피 2잔 추가됨. 소계: 60,000원

---

### 4. Payment

Select payment method:

text 1. Cash 2. Card

---

### 5. Completion

Example:

text 결제가 완료되었습니다. 감사합니다!

---

## 🚀 Running the Project

### Requirements

- Java 17 or later
- Internet connection
- NAVER CLOVA Speech API credentials

---

### Compile

bash javac Main.java CoffeeMachineAI.java

### Run

bash java Main

---

## 📝 Logging

The application automatically records errors and warnings into:

text coffeemachine.log

Logged events include:

- Recording failures
- STT API failures
- Invalid responses
- Runtime exceptions

---

## ⚠️ Notes

- The current implementation records audio for 5 seconds.
- Speech recognition requires a valid CLOVA Speech API Secret Key.
- If the API Secret is invalid, speech recognition will fail.
- Stable internet connectivity is required.

---

## 🧪 Test Scenarios

| Voice Command       | Expected Result                |
| ------------------- | ------------------------------ |
| 포장하기            | Take-out selected              |
| 아메리카노 두 잔    | Coffee and quantity recognized |
| 카드로 결제할게요   | Card payment selected          |
| Recognition failure | Retry process activated        |

---

## 📊 Experimental Results

The system was evaluated using:

- 100 Korean speakers
- 500 voice command samples

### Recognition Accuracy

| Task              | Accuracy |
| ----------------- | -------- |
| Menu Selection    | 95.3%    |
| Coffee Ordering   | 89.7%    |
| Payment Selection | 85.6%    |
| Average           | 90.2%    |

### Response Time

| System            | Response Time |
| ----------------- | ------------- |
| CoffeeMachine     | 6.5 sec       |
| Text-Based System | 3.2 sec       |

Although slightly slower than traditional text-based systems, CoffeeMachine significantly improves accessibility for users with disabilities.

---

## 🔮 Future Improvements

- Text-to-Speech (TTS) voice responses
- GUI-based kiosk interface
- Multiple language support
- Database integration
- Order history management
- Recommendation system using AI
- Emotion-aware customer interaction

---

## 👨‍💻 Author

Developed by:

- 김우석
- 트란트룽하우
- 신지호
- 구민준
- 조민경
- 고영준

AI Voice Coffee Ordering Project

---

## 📄 License

This project was developed for educational and research purposes.

> 본 프로젝트는 황우섭 교수님의 「객체지향프로그래밍및실습」 수업의 일환으로 수행되었습니다.
