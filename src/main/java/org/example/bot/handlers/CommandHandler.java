    package org.example.bot.handlers;

    import com.google.zxing.BarcodeFormat;
    import com.google.zxing.WriterException;
    import com.google.zxing.client.j2se.MatrixToImageWriter;
    import com.google.zxing.common.BitMatrix;
    import com.google.zxing.qrcode.QRCodeWriter;
    import org.apache.poi.ss.usermodel.Cell;
    import org.apache.poi.ss.usermodel.Row;
    import org.apache.poi.ss.usermodel.Sheet;
    import org.apache.poi.ss.usermodel.Workbook;
    import org.apache.poi.xssf.usermodel.XSSFWorkbook;
    import org.apache.poi.xwpf.usermodel.XWPFDocument;
    import org.apache.poi.xwpf.usermodel.XWPFParagraph;
    import org.apache.poi.xwpf.usermodel.XWPFRun;
    import org.example.bot.MyBot;
    import org.example.enumerators.UserState;
    import org.example.exception.DataNotFoundException;
    import org.example.model.User;
    import org.example.model.Vacancy;
    import org.example.repository.UserRepository;
    import org.example.repository.VacancyRepository;
    import org.example.service.UserService;
    import org.example.service.VacancyService;
    import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
    import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
    import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
    import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
    import org.telegram.telegrambots.meta.api.objects.InputFile;
    import org.telegram.telegrambots.meta.api.objects.Message;
    import org.telegram.telegrambots.meta.api.objects.Update;
    import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
    import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
    import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
    import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
    import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

    import javax.activation.DataHandler;
    import javax.activation.DataSource;
    import javax.activation.FileDataSource;
    import javax.imageio.ImageIO;
    import javax.mail.*;
    import javax.mail.internet.InternetAddress;
    import javax.mail.internet.MimeBodyPart;
    import javax.mail.internet.MimeMessage;
    import javax.mail.internet.MimeMultipart;
    import java.awt.image.BufferedImage;
    import java.io.*;
    import java.nio.file.Files;
    import java.nio.file.Paths;
    import java.util.*;



    import static org.example.bot.MyBot.*;
    import static org.telegram.telegrambots.meta.api.objects.Message.*;

    public class CommandHandler {

        RegistrationHandler registrationHandler = new RegistrationHandler();
        VacancyService vacancyService = new VacancyService(new VacancyRepository());
        MyBot myBot;

        public CommandHandler(MyBot myBot) {
            this.myBot = myBot;
        }


        public SendMessage handle(Message message) {
            String text = message.getText();
            Long chatId = message.getChatId();


            if (Objects.equals(text, "/start")) {
                return handleStart(chatId, message.getFrom());
            }

            User currentUser = userService.findByChatId(chatId);

            if(Objects.equals(text,"Back")){
                currentUser.setState(UserState.REGISTERED);
                userService.update(currentUser);
                SendMessage sendMessage = new SendMessage(chatId.toString(),"Back to Menu");
                sendMessage.setReplyMarkup(buttons.getUserMenu());
                return sendMessage;
            }

            switch (currentUser.getState()) {
                case SHARE_CONTACT -> {
                    return registrationHandler.handleContact(message, currentUser);
                }
                case SHARE_LOCATION -> {
                    return registrationHandler.handleLocation(message, currentUser);
                }
                case REGISTERED, MAIN_MANU -> {
                    return handleUserMenu(message, currentUser);
                }
                case USER_VACANCIES -> {
                    if ("Back".equals(text)) {
                        return handleBackButton(chatId, currentUser);
                    }
                    return handleVacancies(chatId, text);
                }

                case CREATING_VACANCY_TITLE -> {
                    currentUser.setCreatingVacancyTitle(text);
                    currentUser.setState(UserState.CREATING_VACANCY_COMPANY);
                    userService.update(currentUser);
                    return new SendMessage(chatId.toString(), "Please enter the company name 🏛 :");
                }
                case CREATING_VACANCY_COMPANY -> {
                    currentUser.setCreatingVacancyCompany(text);
                    currentUser.setState(UserState.CREATING_VACANCY_EXPERIENCE);
                    userService.update(currentUser);
                    return new SendMessage(chatId.toString(), "Please enter the required experience 🗽:");
                }
                case CREATING_VACANCY_EXPERIENCE -> {
                    currentUser.setCreatingVacancyExperience(text);
                    currentUser.setState(UserState.CREATING_VACANCY_SALARY);
                    userService.update(currentUser);
                    return new SendMessage(chatId.toString(), "Please enter the salary 💲:");
                }
                case CREATING_VACANCY_SALARY -> {
                    currentUser.setCreatingVacancySalary(text);
                    currentUser.setState(UserState.CREATING_VACANCY_PHONE);
                    userService.update(currentUser);
                    return new SendMessage(chatId.toString(), "Please enter the company phone number 📞:");
                }
                case CREATING_VACANCY_PHONE -> {
                    currentUser.setCreatingVacancyPhone(text);
                    currentUser.setState(UserState.CREATING_VACANCY_REGION);
                    userService.update(currentUser);
                    Vacancy vacancy = new Vacancy(currentUser.getCreatingVacancyTitle(), currentUser.getCreatingVacancyCompany(), currentUser.getCreatingVacancyExperience(), currentUser.getCreatingVacancySalary(), currentUser.getCreatingVacancyPhone());
                    vacancyService.add(vacancy);
                    return new SendMessage(chatId.toString(), "Please enter vacancy region 🌎: ");
                }
                case CREATING_VACANCY_REGION -> {
                    currentUser.setCreatingVacancyRegion(text);
                    currentUser.setState(UserState.MAIN_MENU);
                    userService.update(currentUser);
                    Vacancy vacancy = new Vacancy(
                            currentUser.getCreatingVacancyTitle(),
                            currentUser.getCreatingVacancyCompany(),
                            currentUser.getCreatingVacancyExperience(),
                            currentUser.getCreatingVacancySalary(),
                            currentUser.getCreatingVacancyPhone(),
                            currentUser.getCreatingVacancyRegion());
                    vacancyService.add(vacancy);


                    //   sendVacancyToChannel(vacancy);
                    return new SendMessage(chatId.toString(), "Your vacancy has been created successfully 😊!");
                }
                case SELECTED_VACANCIES -> {

                }
                case CREATING_RESUME_NAME -> {
                    currentUser.setResumeName(text);
                    currentUser.setState(UserState.CREATING_RESUME_AGE);
                    userService.update(currentUser);
                    return new SendMessage(chatId.toString(), "Please enter your age 😊:");
                }
                case CREATING_RESUME_AGE -> {
                    currentUser.setResumeAge(text);
                    currentUser.setState(UserState.CREATING_RESUME_EDUCATION);
                    userService.update(currentUser);
                    return new SendMessage(chatId.toString(), "Please enter your education 🙎‍♂️:");
                }
                case CREATING_RESUME_EDUCATION -> {
                    currentUser.setResumeEducation(text);
                    currentUser.setState(UserState.CREATING_RESUME_EXPERIENCE);
                    userService.update(currentUser);
                    return new SendMessage(chatId.toString(), "Please enter your work experience 💪:");
                }
                case CREATING_RESUME_EXPERIENCE -> {
                    currentUser.setResumeExperience(text);
                    currentUser.setState(UserState.MAIN_MANU);
                    userService.update(currentUser);
                    saveResumeToFile(currentUser);
                    saveResumeToWordFile(currentUser);
                    SendMessage emailRequestMessage = new SendMessage(chatId.toString(), "Please enter your email address to send the resume 📩 ");
                    currentUser.setState(UserState.SEND_EMAIL);
                    userService.update(currentUser);
                    return emailRequestMessage;

                    //return new SendMessage(chatId.toString(), "Your resume has been created successfully!");

                }
                case SEND_EMAIL -> {
                    String emailAddress = text;
                    String filePath = "src/main/resources/" + currentUser.getUsername() + "_resume.docx";
                    sendResumeByEmail(emailAddress, "Your Resume", "Here is your resume.", filePath);
                    return new SendMessage(chatId.toString(), "Your resume has been sent to " + emailAddress);

                }
                //            case AWAITING_APPLY_TEXT -> {
                //                //  sendApplyEmail(currentUser, text);
                //                sendApplyEmail2(currentUser, text);
                //                currentUser.setState(UserState.MAIN_MANU);
                //                userService.update(currentUser);
                //                return new SendMessage(chatId.toString(), "Your application has been sent!");
                //            }
            }
            return new SendMessage(chatId.toString(), "Wrong command or something went wrong !!! ");

        }


        private SendMessage handleBackButton(Long chatId, User currentUser) {
            currentUser.setState(UserState.MAIN_MANU);
            userService.update(currentUser);
            SendMessage sendMessage = new SendMessage(chatId.toString(), "Back to main menu:");
            sendMessage.setReplyMarkup(buttons.getUserMenu());
            return sendMessage;
        }


        public SendMessage handleUserMenu(Message message, User currentUser) {
            String text = message.getText();
            Long chatId = message.getChatId();
            if (Objects.equals(text, "See Vacancies from regions 📍")) {
                SendMessage sendMessage = new SendMessage(chatId.toString(), String.format("Choose one region for vacancies 📍"));
                sendMessage.setReplyMarkup(buttons.getVacancyList());
                currentUser.setState(UserState.USER_VACANCIES);
                userService.update(currentUser);
                return sendMessage;


                //            return handleVacancies(chatId,text);
            } else if (Objects.equals(text, "Create own vacancy 📂")) {
                //            currentUser.setState(UserState.CREATING_VACANCY_TITLE);
                //            userService.update(currentUser);
                //            return new SendMessage(chatId.toString(), "Please enter the vacancy title:");
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<InlineKeyboardButton> row = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("Create");
                button.setCallbackData("create_vacancy");
                row.add(button);
                List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
                keyboard.add(row);
                inlineKeyboardMarkup.setKeyboard(keyboard);

                SendMessage sendMessage = new SendMessage(chatId.toString(), "Push button for create Vacancy 👇");
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                return sendMessage;


            } else if (Objects.equals(text, "Card number for Donate \uD83D\uDE0A \uD83D\uDCB2")) {
                SendMessage sendMessage = new SendMessage(chatId.toString(), String.format("Your donate is important to make the bot better and more useful 😊 \nCard number : 986000000000000 \nPalonchiyev Pistonchi"));
                InlineKeyboardButton donateButton = new InlineKeyboardButton();
                donateButton.setText("Donate 😎");
                donateButton.setUrl("https://click.uz/uz/perevod-s-karti-na-kartu");

                List<InlineKeyboardButton> row = new ArrayList<>();
                row.add(donateButton);

                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                rows.add(row);

                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                inlineKeyboardMarkup.setKeyboard(rows);

                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                return sendMessage;
            } else if (Objects.equals(text, "Create Resume 📜")) {

                currentUser.setState(UserState.CREATING_RESUME_NAME);
                userService.update(currentUser);
                return new SendMessage(chatId.toString(), "Please enter your name 😊:");

            } else if (Objects.equals(text, "Download All Vacancy 📦")) {
                InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
                List<List<InlineKeyboardButton>> rows = new ArrayList<>();
                List<InlineKeyboardButton> row1 = new ArrayList<>();
                InlineKeyboardButton wordButton = new InlineKeyboardButton();
                wordButton.setText("Download Word File");
                wordButton.setCallbackData("download_word");
                row1.add(wordButton);
                List<InlineKeyboardButton> row2 = new ArrayList<>();
                InlineKeyboardButton excelButton = new InlineKeyboardButton();
                excelButton.setText("Download Excel File");
                excelButton.setCallbackData("download_excel");
                row2.add(excelButton);
                rows.add(row1);
                rows.add(row2);
                inlineKeyboardMarkup.setKeyboard(rows);

                SendMessage sendMessage = new SendMessage(chatId.toString(), "Choose the file format 📌");
                sendMessage.setReplyMarkup(inlineKeyboardMarkup);
                return sendMessage;
            } else if (Objects.equals(text, "Selected vacancies 💼")) {
                return handleSelectedVacancies(chatId, currentUser);

            }
            else if (Objects.equals(text, "Join Channel 🔔")) {
                return handleJoinChannel(chatId);
            }
            return new SendMessage(chatId.toString(), "Went Wrong");
        }


        private SendMessage handleSelectedVacancies(Long chatId, User currentUser) {
            Set<UUID> appliedVacancyIds = currentUser.getAppliedVacancies();
            List<Vacancy> appliedVacancies = vacancyService.getVacanciesByIds(appliedVacancyIds);
            List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

            StringBuilder response = new StringBuilder("📝 Your Selected Vacancies:\n\n");
            for (Vacancy vacancy : appliedVacancies) {
                response.append("✅ Title: ").append(vacancy.getTitle()).append("\n");
                response.append("🏛 Company: ").append(vacancy.getCompany()).append("\n");
                response.append("📂 Experience: ").append(vacancy.getExperience()).append("\n");
                response.append("💲 Salary: ").append(vacancy.getSalary()).append("\n");
                response.append("📞 Phone number company: ").append(vacancy.getPhoneNumberCompany()).append("\n\n");

            }
              //  InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
              List<InlineKeyboardButton> row = new ArrayList<>();
              InlineKeyboardButton button = new InlineKeyboardButton();
              button.setText("Word formatda yuklab olish");
              button.setCallbackData("download_words");
              row.add(button);
              inlineButtons.add(row);

              List<InlineKeyboardButton> row2 = new ArrayList<>();
              InlineKeyboardButton button1 = new InlineKeyboardButton();
              button1.setText("Excel formatda yuklab olish");
              button1.setCallbackData("download_excel_file");
              row2.add(button1);
              inlineButtons.add(row2);


            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(inlineButtons);

            SendMessage sendMessage = new SendMessage(chatId.toString(), response.toString());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            sendMessage.setParseMode("Markdown");
            return sendMessage;
        }

        private SendMessage handleVacancies(Long chatId, String region) {
            List<Vacancy> vacancies = vacancyService.getVacanciesByRegion(region);
            StringBuilder response = new StringBuilder("🔍 Vacancy:\n\n");
            List<List<InlineKeyboardButton>> inlineButtons = new ArrayList<>();

            for (Vacancy vacancy : vacancies) {
                response.append("✅ Title: ").append(vacancy.getTitle()).append("\n");
                response.append("🏛 Company: ").append(vacancy.getCompany()).append("\n");
                response.append("📂 Experience: ").append(vacancy.getExperience()).append("\n");
                response.append("💲 Salary: ").append(vacancy.getSalary()).append("\n\n");
                response.append("📞 Phone number company : ").append(vacancy.getPhoneNumberCompany()).append("\n\n");
                response.append("Photo : ").append("[Link](").append(vacancy.getPhotoUrl()).append(")\n");

                List<InlineKeyboardButton> row = new ArrayList<>();
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText("Send Apply 🔔");
                button.setCallbackData("apply_" + vacancy.getId().toString());
                row.add(button);
                inlineButtons.add(row);

            }

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(inlineButtons);

            SendMessage sendMessage = new SendMessage(chatId.toString(), response.toString());
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
            sendMessage.setParseMode("Markdown");
            return sendMessage;
        }

        public void saveResumeToFile(User user) {
            String fileName = "src/main/resources/" + user.getUsername() + "_resume.txt";
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write("Name: " + user.getResumeName() + "\n");
                writer.write("Age: " + user.getResumeAge() + "\n");
                writer.write("Education: " + user.getResumeEducation() + "\n");
                writer.write("Work Experience: " + user.getResumeExperience() + "\n");
                System.out.println("Resume saved to " + fileName);
            } catch (IOException e) {
                System.out.println("An error occurred while saving the resume.");
                e.printStackTrace();
            }
        }


        public SendMessage handleStart(Long chatId, org.telegram.telegrambots.meta.api.objects.User from) {
            User user = null;
            try {
                user = userService.findByChatId(chatId);
                user.setState(UserState.MAIN_MANU);
                userService.update(user);
                user.setState(UserState.MAIN_MANU);
                SendMessage sendMessage = new SendMessage(chatId.toString(),
                        String.format("Welcome back %s, choose one title 📌", user.getUsername()));
                sendMessage.setReplyMarkup(buttons.getUserMenu());
                return sendMessage;
            } catch (DataNotFoundException e) {
                //log.info(e.getMessage(), chatId);
                User newUser = User.builder()
                        .username(from.getFirstName())
                        .lastname(from.getLastName())
                        .username(from.getUserName())
                        .state(UserState.SHARE_CONTACT)
                        .chatId(chatId).build();
                user = userService.add(newUser);
            }
            SendMessage sendMessage = new SendMessage(chatId.toString(),
                    String.format("Welcome %s, please share your number 📞", user.getUsername()));
            sendMessage.setReplyMarkup(buttons.requestContact());
            return sendMessage;
        }


        ////

        public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
            Long chatId = callbackQuery.getMessage().getChatId();
            String data = callbackQuery.getData();
            User currentUser = userService.findByChatId(chatId);
            String[] callBackInfos = data.split("_");
            if ("apply".equals(callBackInfos[0])) {
                UUID vacancyId = UUID.fromString(callBackInfos[1]);
                currentUser.addAppliedVacancy(vacancyId);
                userService.update(currentUser);
                return new SendMessage(chatId.toString(), "Thank you.You choose this vacancy 😊");
            }
            if ("create_vacancy".equals(data)) {
                currentUser.setState(UserState.CREATING_VACANCY_TITLE);
                userService.update(currentUser);
                return new SendMessage(chatId.toString(), "Please enter the vacancy title ✏:");
            } else if (data.equals("download_word")) {
                return handleWordFileDownload(chatId);
            } else if (data.equals("download_excel")) {
                return handleExcelFileDownload(chatId);
            } else if (data.equals("download_words")) {
                return handleSelectedVacanciesWordDownload(chatId, currentUser);
            } else if ("download_excel_file".equals(data)) {
                return handleSelectedVacanciesExcelDownload(chatId, currentUser);
            }

            return new SendMessage(chatId.toString(), "Unknown callback query data.");
        }

        private SendMessage handleWordFileDownload(Long chatId) {
            String filePath = generateVacancyWordFile();
            sendWordFile(chatId, filePath);
            return new SendMessage(chatId.toString(), "Word file with all vacancies has been sent to you 😊.");
        }

        private SendMessage handleExcelFileDownload(Long chatId) {
            String filePath = generateVacancyExcelFile();
            sendExcelFile(chatId, filePath);
            return new SendMessage(chatId.toString(), "Excel file with all vacancies has been sent to you 😊.");
        }


        private String generateVacancyWordFile() {
            List<Vacancy> vacancies = vacancyService.getAll();
            String fileName = "src/main/resources/vacancies.docx";
            try (XWPFDocument document = new XWPFDocument()) {
                for (Vacancy vacancy : vacancies) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setText("Title: " + vacancy.getTitle());
                    run.addBreak();
                    run.setText("Company: " + vacancy.getCompany());
                    run.addBreak();
                    run.setText("Experience: " + vacancy.getExperience());
                    run.addBreak();
                    run.setText("Salary: " + vacancy.getSalary());
                    run.addBreak();
                    run.setText("Phone: " + vacancy.getPhoneNumberCompany());
                    run.addBreak();
                    run.setText("Region: " + vacancy.getRegion());
                    run.addBreak();
                    run.addBreak();
                }
                try (FileOutputStream out = new FileOutputStream(fileName)) {
                    document.write(out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileName;
        }

        private void sendWordFile(Long chatId, String filePath) {
            SendDocument sendDocumentRequest = new SendDocument();
            sendDocumentRequest.setChatId(chatId.toString());
            sendDocumentRequest.setDocument(new InputFile(new java.io.File(filePath)));


            try {
                myBot.execute(sendDocumentRequest); // Use the bot instance to call execute
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


        private void sendApplyEmail2(User currentUser, String applicationText) {
            String to = "behzodfarhodov31@gmail.com";
            String subject = "New Application from " + currentUser.getUsername();
            String text = "User " + currentUser.getUsername() + " has sent the following application:\n\n" + applicationText;
            EmailSender.sendEmail(to, subject, text);
        }


        public static class EmailSender {
            private static final String username = "behzodfarhodov13@gmail.com";
            private static final String password = "wlhdpdupbylmcizh";

            public static void sendEmail(String to, String subject, String text) {
                Properties properties = new Properties();
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");


                Session session = Session.getInstance(properties, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

                try {
                    MimeMessage message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.addRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse(to));
                    message.setSubject(subject);
                    message.setText(text);

                    Transport.send(message);
                    System.out.println("Email sent successfully!");

                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }


        }


        ////


        public void saveResumeToWordFile(User user) {
            String wordFileName = "src/main/resources/" + user.getUsername() + "_resume.docx";
            try (XWPFDocument document = new XWPFDocument()) {
                XWPFParagraph paragraph = document.createParagraph();
                XWPFRun run = paragraph.createRun();
                run.setText("Name: " + user.getResumeName());
                run.addBreak();
                run.setText("Age: " + user.getResumeAge());
                run.addBreak();
                run.setText("Education: " + user.getResumeEducation());
                run.addBreak();
                run.setText("Work Experience: " + user.getResumeExperience());
                run.addBreak();

                try (FileOutputStream out = new FileOutputStream(wordFileName)) {
                    document.write(out);
                }

                System.out.println("Word resume saved to " + wordFileName);
            } catch (IOException e) {
                System.out.println("An error occurred while saving the Word resume.");
                e.printStackTrace();
            }
        }


        public void sendResumeByEmail(String toEmail, String subject, String text, String filePath) {
            final String fromEmail = "behzodfarhodov31@gmail.com";
            final String password = "wlhdpdupbylmcizh";
            String host = "smtp.live.com";


            Properties properties = new Properties();
            //        properties.put("mail.smtp.host", "smtp.gmail.com");
            //        properties.put("mail.smtp.port", "587");
            //        properties.put("mail.smtp.auth", "true");
            //        properties.put("mail.smtp.starttls.enable", "true");
            //        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            //        properties.setProperty("mail.session.mail.smtp.auth.mechanisms", "LOGIN");
            //        properties.setProperty("mail.session.mail.smtp.auth.plain.disable", "false");
            //
            //        properties.setProperty("mail.session.mail.smtp.starttls.enable", "false");
            //        properties.put("mail.smtp.connectiontimeout", 6000);


//            properties.put("mail.smtp.host", "smtp.gmail.com");
//            properties.put("mail.smtp.port", "587");
//            properties.put("mail.smtp.auth", "true");
//            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
//            properties.put("mail.smtp.starttls.enable", "true");
//            properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.ssl.enable", "true");


            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            try {
                System.out.println(toEmail);
                MimeMessage mimeMessage = new MimeMessage(session);
                mimeMessage.setFrom(new InternetAddress(fromEmail));
                mimeMessage.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(toEmail));
                mimeMessage.setSubject(subject);
             //   mimeMessage.setText("Hello bro this is your resume");

                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(text);

//                Multipart multipart = new MimeMultipart();
//                multipart.addBodyPart(messageBodyPart);
//
//                messageBodyPart = new MimeBodyPart();
//                DataSource source = new FileDataSource(filePath);
//                messageBodyPart.setDataHandler(new DataHandler(source));
//                messageBodyPart.setFileName(filePath);
//                multipart.addBodyPart(messageBodyPart);

                MimeBodyPart mimeBodyPart = new MimeBodyPart();
                DataSource source = new FileDataSource(filePath);
                mimeBodyPart.setDataHandler(new DataHandler(source));
                mimeBodyPart.setFileName(filePath);
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(mimeBodyPart);

                mimeMessage.setContent(multipart);

                Transport.send(mimeMessage);
                System.out.println("Email sent successfully 🚀.");

            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }


        ////

        private String generateVacancyExcelFile() {
            List<Vacancy> vacancies = vacancyService.getAll();
            String fileName = "src/main/resources/vacancies.xlsx";
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Vacancies");

                Row headerRow = sheet.createRow(0);
                String[] headers = {"Title", "Company", "Experience", "Salary", "Phone", "Region"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }

                int rowNum = 1;
                for (Vacancy vacancy : vacancies) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(vacancy.getTitle());
                    row.createCell(1).setCellValue(vacancy.getCompany());
                    row.createCell(2).setCellValue(vacancy.getExperience());
                    row.createCell(3).setCellValue(vacancy.getSalary());
                    row.createCell(4).setCellValue(vacancy.getPhoneNumberCompany());
                    row.createCell(5).setCellValue(vacancy.getRegion());
                }

                try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                    workbook.write(fileOut);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileName;
        }

        private SendMessage handleAllVacanciesInExcel(Long chatId) {
            String filePath = generateVacancyExcelFile();
            sendExcelFile(chatId, filePath);
            return new SendMessage(chatId.toString(), "Excel file with all vacancies has been sent to you 🚀.");
        }

        private void sendExcelFile(Long chatId, String filePath) {
            SendDocument sendDocumentRequest = new SendDocument();
            sendDocumentRequest.setChatId(chatId);
            sendDocumentRequest.setDocument(new InputFile(new java.io.File(filePath)));
            try {
                myBot.execute(sendDocumentRequest);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }


        //////selected vacancylani methodi



        private SendMessage handleSelectedVacanciesWordDownload(Long chatId, User currentUser) {
            String filePath = generateSelectedVacanciesWordFile(currentUser);
            sendDocument(chatId, filePath, "Tanlangan vakansiyalar Word fayli");
            return new SendMessage(chatId.toString(), "Tanlangan vakansiyalar Word formatida jo'natildi 😊.");
        }

        private SendMessage handleSelectedVacanciesExcelDownload(Long chatId, User currentUser) {
            String filePath = generateSelectedVacanciesExcelFile(currentUser);
            sendDocument(chatId, filePath, "Tanlangan vakansiyalar Excel fayli");
            return new SendMessage(chatId.toString(), "Tanlangan vakansiyalar Excel formatida jo'natildi 😊.");
        }

        private String generateSelectedVacanciesWordFile(User currentUser) {
            List<Vacancy> appliedVacancies = vacancyService.getVacanciesByIds(currentUser.getAppliedVacancies());
            String fileName = "src/main/resources/selected_vacancies.docx";
            try (XWPFDocument document = new XWPFDocument()) {
                for (Vacancy vacancy : appliedVacancies) {
                    XWPFParagraph paragraph = document.createParagraph();
                    XWPFRun run = paragraph.createRun();
                    run.setText("Title: " + vacancy.getTitle());
                    run.addBreak();
                    run.setText("Company: " + vacancy.getCompany());
                    run.addBreak();
                    run.setText("Experience: " + vacancy.getExperience());
                    run.addBreak();
                    run.setText("Salary: " + vacancy.getSalary());
                    run.addBreak();
                    run.setText("Phone: " + vacancy.getPhoneNumberCompany());
                    run.addBreak();
                    run.setText("Region: " + vacancy.getRegion());
                    run.addBreak();
                    run.addBreak();
                }
                try (FileOutputStream out = new FileOutputStream(fileName)) {
                    document.write(out);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileName;
        }

        private String generateSelectedVacanciesExcelFile(User currentUser) {
            List<Vacancy> appliedVacancies = vacancyService.getVacanciesByIds(currentUser.getAppliedVacancies());
            String fileName = "src/main/resources/selected_vacancies.xlsx";
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Selected Vacancies");

                Row headerRow = sheet.createRow(0);
                String[] headers = {"Title", "Company", "Experience", "Salary", "Phone", "Region"};
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }

                int rowNum = 1;
                for (Vacancy vacancy : appliedVacancies) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(vacancy.getTitle());
                    row.createCell(1).setCellValue(vacancy.getCompany());
                    row.createCell(2).setCellValue(vacancy.getExperience());
                    row.createCell(3).setCellValue(vacancy.getSalary());
                    row.createCell(4).setCellValue(vacancy.getPhoneNumberCompany());
                    row.createCell(5).setCellValue(vacancy.getRegion());
                }

                try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
                    workbook.write(fileOut);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileName;
        }

        private void sendDocument(Long chatId, String filePath, String caption) {
            SendDocument sendDocumentRequest = new SendDocument();
            sendDocumentRequest.setChatId(chatId.toString());
            sendDocumentRequest.setDocument(new InputFile(new java.io.File(filePath)));
            sendDocumentRequest.setCaption(caption);

            try {
                myBot.execute(sendDocumentRequest);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }


            //Send Apply qilganda

        }

        ///
        private SendMessage handleJoinChannel(Long chatId) {
            try {
                String channelLink = "https://t.me/vacancyopenn";
                byte[] qrCodeImage =generateQRCodeImage(channelLink, 300, 300);
                InputFile inputFile = new InputFile(new ByteArrayInputStream(qrCodeImage), "qr_code.png");

                SendPhoto sendPhotoRequest = new SendPhoto();
                sendPhotoRequest.setChatId(chatId.toString());
                sendPhotoRequest.setPhoto(inputFile);


                myBot.execute(sendPhotoRequest);
            } catch (WriterException | IOException | TelegramApiException e) {
                e.printStackTrace();
                return new SendMessage(chatId.toString(), "Failed to generate QR code.");
            }
            return new SendMessage(chatId.toString(), "Scan this QR code to join our channel 😃");
        }



        public static byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }


//        public void sendJoinChannelButton(Long chatId) {
//            InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
//            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
//            List<InlineKeyboardButton> rowInline = new ArrayList<>();
//            rowInline.add(new InlineKeyboardButton().setText("Join Channel").setUrl("https://t.me/your_channel_link"));
//            rowsInline.add(rowInline);
//            markupInline.setKeyboard(rowsInline);
//            SendMessage message = new SendMessage().setChatId(chatId.toString()).setText("Join our channel:").setReplyMarkup(markupInline);
//            try {
//                myBot.execute(message);
//            } catch (TelegramApiException e) {
//                e.printStackTrace();
//            }
//        }









    }


