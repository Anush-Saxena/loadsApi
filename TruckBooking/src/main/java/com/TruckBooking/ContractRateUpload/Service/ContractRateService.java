package com.TruckBooking.ContractRateUpload.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.TruckBooking.ContractRateUpload.Dao.ContractRateRepo;
import com.TruckBooking.ContractRateUpload.Dao.IndentDao;
import com.TruckBooking.ContractRateUpload.Entity.Indent;
import com.TruckBooking.ContractRateUpload.Entity.Indent.TransporterStatus;
import com.TruckBooking.ContractRateUpload.Entity.Rates;
import com.TruckBooking.TruckBooking.Dao.LoadDao;
import com.TruckBooking.TruckBooking.Dao.TransporterEmailDao;
import com.TruckBooking.TruckBooking.Entities.Load;
import com.TruckBooking.TruckBooking.Entities.Load.Publish;
import com.TruckBooking.TruckBooking.Entities.Load.Status;
import com.TruckBooking.TruckBooking.Service.LoadServiceImpl;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractRateService {

	@Autowired
	LoadDao loadDao;

	@Autowired
	TransporterEmailDao transporterEmailDao;

	@Autowired
    ContractRateRepo contractpricerepo;

	@Autowired
    IndentDao rankrepo;

	@Autowired
	private JavaMailSender mailSender;

    // check that file is of excel type or not
    public static boolean checkExcelFormat(MultipartFile file) {

        String contentType = file.getContentType();

        if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return true;
        } else {
            return false;
        }

    }

    // convert excel to list of products

    public static List<Rates> convertExcelToListOfProduct(InputStream is) {
        List<Rates> list = new ArrayList<>();

        try {

            try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
                XSSFSheet sheet = workbook.getSheet("Sheet1");

                int rowNumber = 0;
                Iterator<Row> iterator = sheet.iterator();

                while (iterator.hasNext()) {
                    Row row = iterator.next();
                    int flag = 0;
                    if (rowNumber == 0) {
                        rowNumber++;
                        continue;
                    }

                    Iterator<Cell> cells = row.iterator();

                    int cid = 0;

                    Rates p = new Rates();

                    while (cells.hasNext()) {
                        Cell cell = cells.next();

                        switch (cid) {
                            case 0:
                                try {
                                    p.setUnLoadingPoint(cell.getStringCellValue());
                                } catch (Exception e) {
                                    System.out.println("UNP");
                                    flag = 1;
                                }
                                break;
                            case 1:
                                try {
                                    p.setWeight(String.valueOf((int) cell.getNumericCellValue()));
                                } catch (Exception e) {
                                    System.out.println("weight");
                                    flag = 1;
                                }
                                break;
                            case 2:
                                try {
                                    p.setRate((int) cell.getNumericCellValue());
                                } catch (Exception e) {
                                    System.out.println("Rate");
                                    flag = 1;
                                }
                                break;
                            case 3:
                                try {
                                    p.setTransporterId(String.valueOf((int) cell.getNumericCellValue()));
                                } catch (Exception e) {
                                    System.out.println("Tid");
                                    flag = 1;
                                }
                                break;
                            case 4:
                                try {
                                    p.setTransporterEmail(cell.getStringCellValue());
                                } catch (Exception e) {
                                    System.out.println("Email");
                                    flag = 1;
                                }
                                break;
                            case 5:
                                try {
                                    p.setTransporterName(cell.getStringCellValue());
                                } catch (Exception e) {
                                    System.out.println("TraN");
                                    flag = 1;
                                }
                                break;
                            case 6:
                                try {
                                    p.setLoadingPoint(cell.getStringCellValue());
                                } catch (Exception e) {
                                    System.out.println("Loap");
                                    flag = 1;
                                }
                                break;
                            default:
                                break;
                        }
                        cid++;
                    }

                    if (flag == 1) {
                        continue;
                    } else {
                        list.add(p);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out.print(list.size());
        return list;
    }

    // this function helps us to save excel file.
    public void save(MultipartFile file) {

        try {
            List<Rates> products = ContractRateService.convertExcelToListOfProduct(file.getInputStream());
            this.contractpricerepo.saveAll(products);
            System.out.println("Saved");
        } catch (IOException e) {
            System.out.println("Error is error");
            e.printStackTrace();
        }
    }

    public List<String> getAllPrice(String unLoadingPoint, String weight, String loadid) {

        List<Rates> list = this.contractpricerepo.findByUnLoadingPointAndWeightOrderByRateAsc(unLoadingPoint, weight);
        List<String> transport = new ArrayList<>();

        for (Rates i : list) {
            transport.add(i.getTransporterId());
        }

        if (list.isEmpty()) {
            return transport;
        }

        Indent res = new Indent(loadid, transport, (list.get(0)).getTransporterId(),
                (list.get(0).getTransporterEmail()), TransporterStatus.ON_GOING);
        Optional<Load> optionalLoad = loadDao.findByLoadId(loadid);
        if (optionalLoad.isPresent()) {
            Load load = optionalLoad.get();
            load.setStatus(Status.ON_GOING);
            loadDao.save(load);
        }
        this.rankrepo.save(res);
        // System.out.println(list.get(0));

        return transport;
    }

    // Helps to send emails.
    public static void sendSimpleEmail(JavaMailSender mailSender, String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("mail@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        mailSender.send(message);
        System.out.println("Mail Sent...");
    }

    @Scheduled(fixedRate = 5000)
    public void findRank() {
        List<Load> rank = loadDao.findByPublishAndStatus(Publish.CONTRACT, Status.PENDING);
        List<String> arr = new ArrayList<>();

        for (Load i : rank) {
            arr = getAllPrice(i.getUnloadingPoint(), i.getWeight(), i.getLoadId());
            for (String j : arr) {
                System.out.print(j);
            }
        }
    }

    @Scheduled(cron = "* * * * * *")
    public void triggerMail() throws MessagingException {
        System.out.println("Email function running");
        List<Indent> responses = this.rankrepo.findAll();
        for (Indent it : responses) {
            if ((it.getTransporterStatus()) == TransporterStatus.ON_GOING) {
                System.out.println(it.getTransporterStatus());
                try {
                    sendSimpleEmail(mailSender, it.getEmail(), "Load Assignment",
                            "Load" + it.getLoadId()
                                    + "is assigned to you complete it asap,check Liveasy dashboad for more information.");
                } catch (Exception e) {
                    System.out.println("Mail not sent.");
                }
            }
        }
    }
}