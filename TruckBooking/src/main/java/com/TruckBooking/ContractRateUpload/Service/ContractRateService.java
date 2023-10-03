package com.TruckBooking.ContractRateUpload.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.mail.MessagingException;

import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractRateService {

	@Autowired
	LoadDao loadDao;

	@Autowired
	TransporterEmailDao transporterEmailDao;

	@Autowired
    ContractRateRepo contractPriceRepo;

	@Autowired
    IndentDao rankrepo;

	@Autowired
	private JavaMailSender mailSender;

    // check that file is of excel type or not
    public boolean isExcelFile(MultipartFile file) {

        String contentType = file.getContentType();
        return contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    }

    // convert excel to list of products

    public List<Rates> convertExcelToListOfRates(InputStream is) {
        List<Rates> list = new ArrayList<>();

        try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheet("Sheet1");

            int rowNumber = 0;
            Iterator<Row> rowIterator = sheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                int flag = 0;
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }
                rowNumber++;

                Iterator<Cell> cellIterator = row.iterator();
                int cid = 0;

                Rates p = new Rates();
                try {
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        CellType type = cell.getCellType();
                        System.out.println(cid);
                        System.out.println(cellIterator.hasNext());
                        if(cid==0 || cid==3 ||cid==4 || cid==5 || cid==6 && type.name().equals("String")){

                        }

                       // code 3 is for the blank cells so if a  cell doesn't contains any data then the row won't get saved|
                        if (type.getCode() == 3){
                            log.error("Row "+rowNumber+" Contains some empty entries");
                             break;
                        }

                        // entry at the 0th index is to be shown always as Numeric not empty if it is empty.

                        else if (cid==0 && type.name().equals("NUMERIC")) {
                            log.error("Row "+rowNumber+" Contains some empty entries");
                            break;
                        }

                        switch (cid) {
                            case 0:
                                p.setUnloadingPoint(cell.getStringCellValue());
                                break;
                            case 1:
                                p.setWeight(String.valueOf((int) cell.getNumericCellValue()));
                                break;
                            case 2:
                                p.setRate((int) cell.getNumericCellValue());
                                break;
                            case 3:
                                p.setTransporterId(cell.getStringCellValue());
                                break;
                            case 4:
                                p.setTransporterEmail(cell.getStringCellValue());
                                break;
                            case 5:
                                p.setTransporterName(cell.getStringCellValue());
                                break;
                            case 6:
                                p.setLoadingPoint(cell.getStringCellValue());
                                break;
                            default:
                                break;
                        }
                        cid++;
                    }
                    if (cid>=7){
                        list.add(p);
                    }
                } catch (Exception e){
                    log.error(String.valueOf(e));
                }
            }
        } catch (Exception e) {
            log.error(String.valueOf(e));
        }
        return list;
    }

    // this function helps us to save excel file.
    public boolean save(MultipartFile file) {

        try {
            List<Rates> products = convertExcelToListOfRates(file.getInputStream());
            contractPriceRepo.saveAll(products);
            log.info("Saved");
            return true;
        } catch (Exception e) {
            log.error(String.valueOf(e));
            return false;
        }
    }



    /*public List<String> getAllPrice(String unloadingPoint, String weight, String loadid) {

        List<Rates> list = contractPriceRepo.findByUnloadingPointAndWeightOrderByRateAsc(unloadingPoint, weight);
        List<String> tId = new ArrayList<>();
        List<String>

        for (Rates i : list) {
            tId.add(i.getTransporterId());
        }

        if (list.isEmpty()) {
            return tId;
        }

        Indent res = new Indent(loadid, tId, (list.get(0)).getTransporterId(),
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
    }*/

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

    /*//@Scheduled(fixedRate = 5000)
    public void findRankOriginal() {
        List<Load> rank = loadDao.findByPublishAndStatus(Publish.CONTRACT, Status.PENDING);
        List<String> arr = new ArrayList<>();

        for (Load i : rank) {
            arr = getAllPrice(i.getUnloadingPoint(), i.getWeight(), i.getLoadId());
            for (String j : arr) {
                System.out.print(j);
            }
        }
    }*/
    @Scheduled(fixedRate = 5000)
    public void findRank(){
        List<Load> loads = loadDao.findByPublishMethodAndStatus("Contract",Status.PENDING);
        List<String> tId;
        List<String> tEmail;
        List<Rates> rateList;
        Indent rankedInfo;

        for (Load x : loads){
            rateList = contractPriceRepo.findByLoadingPointAndUnloadingPointAndWeightOrderByRateAsc(x.getLoadingPoint(),x.getUnloadingPoint(),x.getWeight());
            tId = new ArrayList<>();
            tEmail = new ArrayList<>();
            for (Rates y: rateList){
                tId.add(y.getTransporterId());
                tEmail.add(y.getTransporterEmail());
            }
            rankedInfo = new Indent(x.getLoadId(), tId, 0, tEmail, TransporterStatus.ON_GOING);
            x.setStatus(Status.ON_GOING);
            loadDao.save(x);
            rankrepo.save(rankedInfo);
        }

    }

    @Scheduled(cron = "* * * * * *")
    public void triggerMail() throws MessagingException {
        //System.out.println("Email function running");
        List<Indent> responses = this.rankrepo.findAll();
        for (Indent it : responses) {
            if ((it.getTransporterStatus()) == TransporterStatus.ON_GOING) {
                System.out.println(it.getTransporterStatus());
                try {
                    sendSimpleEmail(mailSender, (it.getTransporterEmail()).get(it.getPosition()), "Load Assignment",
                            "Load" + it.getLoadId()
                                    + "is assigned to you complete it asap,check Liveasy dashboard for more information.");
                } catch (Exception e) {
                    System.out.println("Mail not sent.");
                }
            }
        }
    }
}