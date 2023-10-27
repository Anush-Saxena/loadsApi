package com.TruckBooking.ContractRateUpload.Service;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Iterator;

import com.TruckBooking.ContractRateUpload.EmailSender.SendEmail;
import com.google.type.DateTime;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.TruckBooking.ContractRateUpload.Dao.ContractRateRepo;
import com.TruckBooking.ContractRateUpload.Dao.IndentDao;
import com.TruckBooking.ContractRateUpload.Entity.Indent;
import com.TruckBooking.ContractRateUpload.Entity.Rates;
import com.TruckBooking.TruckBooking.Dao.LoadDao;
import com.TruckBooking.TruckBooking.Dao.TransporterEmailDao;
import com.TruckBooking.TruckBooking.Entities.Load;
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
    IndentDao indentDao;

    @Autowired
    private SendEmail email;

    // check that file is of excel type or not
    public boolean isExcelFile(MultipartFile file) {

        String contentType = file.getContentType();
        return contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

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

                       // code 3 is for the blank cells so if a  cell doesn't contains any data then the row won't get saved|
                        if (type.getCode() == 3){
                            log.error("Row "+rowNumber+" Contains some empty entries");
                             break;
                        }

                        // entry at the 0th index is to be shown always as Numeric not empty if it is empty.

//                        else if (cid==0 && type.name().equals("NUMERIC")) {
//                            log.error("Row "+rowNumber+" Contains some empty entries");
//                            break;
//                        }

                        switch (cid) {
                            case 0:
                                p.setUnloadingPointCity(cell.getStringCellValue());
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
                                p.setLoadingPointCity(cell.getStringCellValue());
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

    @Scheduled(fixedRate = 60000)
    public void findRank(){
        System.out.println("Find Rank Running");
        List<Load> loads = loadDao.findByPublishMethodAndStatus("contract",Status.PENDING);
        List<String> transporterId;
        List<String> transporterEmail;
        List<Rates> rateList;
        Indent indentTable;

        for (Load x : loads){
            rateList = contractPriceRepo.findByLoadingPointCityAndUnloadingPointCityAndWeightOrderByRateAsc(x.getLoadingPointCity(),x.getUnloadingPointCity(),x.getWeight());
            if (!rateList.isEmpty()){
                transporterId = new ArrayList<>();
                transporterEmail = new ArrayList<>();
                for (Rates y : rateList) {
                    transporterId.add(y.getTransporterId());
                    transporterEmail.add(y.getTransporterEmail());
                }
                indentTable = new Indent(x.getLoadId(), transporterId, 0, transporterEmail, Status.NOT_ASSIGNED);
                x.setStatus(Status.NOT_ASSIGNED);
                loadDao.save(x);
                indentDao.save(indentTable);
            }
        }

    }

    @Scheduled(fixedRate = 120000)
    public void triggerMail()  {
        System.out.println("Email function running");
        List<Indent> responses = this.indentDao.findAll();
        System.out.println(responses.size());
        for (Indent it : responses) {
            if ((it.getStatus()) == Status.NOT_ASSIGNED) {
                List<String> list = it.getTransporterEmail();
                System.out.println("inside loop"+list.size());
                Load load = loadDao.findByLoadId(it.getLoadId()).get();

                // subject format "indent for 25MT from ambala to delhi"
                String subject = "Indent for "+load.getWeight()+" from "+ load.getLoadingPointCity() +" to "+ load.getUnloadingPointCity();

                // body format  //Asian Paint has posted a load
                                //Loading Point: Mumbai,Mumbai,Maharashtra, India
                                //to
                                //Unloading Point: Delhi Public School Bengaluru South, Road Mango Garden Layout Konanakune,Bengaluru,Karnataka, India
                                //Requirements
                                //Truck Type: TRAILER_BODY
                                //Tyre :6
                                //Weight :25
                                //Product Type :Agriculture and Food
                String body = load.getLoadingPoint()+" has posted a load\n" +
                        "Loading Point: " + load.getLoadingPoint() + ", " + load.getLoadingPointCity() + ", " + load.getLoadingPointState() + ", India\n" +
                        "to\n" +
                        "Unloading Point: " +load.getUnloadingPoint() + ", " + load.getUnloadingPointCity() + ", " + load.getUnloadingPointState() + ", India\n" +
                        "Requirements\n" +
                        "Truck Type: " + load.getTruckType() +"\n" +
                        "Tyre : " + load.getNoOfTyres() + "\n" +
                        "Weight : " + load.getWeight() + "\n" +
                        "Product Type : " + load.getProductType();

//                address = new SendEmail(it.getTransporterEmail().get(it.getPosition()), body, subject);
                email.setAddress(it.getTransporterEmail().get(it.getPosition()));
                email.setBody(body);
                email.setSubject(subject);
                boolean x = email.isSend();
                if (x){
                    it.setStatus(Status.INDENT_ASSIGNED);
                }
                else{
                    // if address of transporter is wrong then system is gonna change the position to the next one
                    // i.e. indent will be assigned to the next transporter immediately
                    if (it.getPosition()+1<list.size()){
                        it.setPosition(it.getPosition() + 1);
                    }
                    else {
                        it.setStatus(Status.TRANSPORTER_REJECTED);// To look here
                    }
                }
                indentDao.save(it);
            }
        }
    }

    @Scheduled(fixedRate = 120000)
    public void checkRejected(){
        List<Indent> rejectedIndents = indentDao.findByStatus(Status.TRANSPORTER_REJECTED);
        System.out.println("Rejected " + rejectedIndents.size());
        if (!rejectedIndents.isEmpty()){
            for (Indent indent: rejectedIndents){
                if (indent.getPosition()+1<indent.getTransporterEmail().size()){
                    indent.setPosition(indent.getPosition()+1);
                    indent.setStatus(Status.NOT_ASSIGNED);
                }
                else{
                    indent.setStatus(Status.TRANSPORTER_REJECTED);// To change here
                }
                indentDao.save(indent);
            }
        }
    }

    // Scheduler to automatically assign the load to next indent
    @Scheduled(fixedRate = 1800000)
    public void checkUnassigned(){
        List<Indent> unassignedIndents = indentDao.findByStatus(Status.INDENT_ASSIGNED);
        System.out.println("Unassigned " + unassignedIndents.size());
        if (!unassignedIndents.isEmpty()){
            // For Getting current time
            Date date = new Date();
            for (Indent indent: unassignedIndents){
                // These lines check if the time duration is greater than 2 hours
                int initial = indent.getAssignedTime().getHours();
                int current = date.getHours();
                if (current - initial < 2){
                    System.out.println("Time limit less than 2 hours");
                    continue;
                }
                if (indent.getPosition()+1<indent.getTransporterEmail().size()){
                    indent.setPosition(indent.getPosition()+1);
                    indent.setStatus(Status.NOT_ASSIGNED);
                }
                else{
                    indent.setStatus(Status.TRANSPORTER_REJECTED);// To change here
                }
                indentDao.save(indent);
            }
        }
    }
}