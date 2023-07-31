package com.TruckBooking.TruckBooking.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.TruckBooking.TruckBooking.Constants.CommonConstants;
import com.TruckBooking.TruckBooking.Dao.LoadDao;
import com.TruckBooking.TruckBooking.Dao.IndentDao;
import com.TruckBooking.TruckBooking.Dao.ContractPriceRepo;
import com.TruckBooking.TruckBooking.Dao.IndentDao;
import com.TruckBooking.TruckBooking.Entities.Load;
import com.TruckBooking.TruckBooking.Entities.Indent;
import com.TruckBooking.TruckBooking.Entities.Rates;
import com.TruckBooking.TruckBooking.Entities.Load.Publish;
import com.TruckBooking.TruckBooking.Entities.Indent.Status;
import com.TruckBooking.TruckBooking.Exception.BusinessException;
import com.TruckBooking.TruckBooking.Exception.EntityNotFoundException;
import com.TruckBooking.TruckBooking.Model.LoadRequest;
import com.TruckBooking.TruckBooking.Response.CreateLoadResponse;
import com.TruckBooking.TruckBooking.Response.UpdateLoadResponse;

import ContractPriceUpload.ContractPriceUpload;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ContractRateService {

	@Autowired
	LoadDao loadDao;

	@Autowired
    ContractPriceRepo contractpricerepo;

	@Autowired
    IndentDao indentdao;

    public class ContractPriceUpload {

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
                                    flag = 1;
                                }
                                break;
                            case 1:
                                try {
                                    p.setWeight((int) cell.getNumericCellValue());
                                } catch (Exception e) {
                                    flag = 1;
                                }
                                break;
                            case 2:
                                try {
                                    p.setRate((int) cell.getNumericCellValue());
                                } catch (Exception e) {
                                    flag = 1;
                                }
                                break;
                            case 3:
                                try {
                                    p.setTransporterId((int) cell.getNumericCellValue());
                                } catch (Exception e) {
                                    flag = 1;
                                }
                                break;
                            case 4:
                                try {
                                    p.setTransporterEmail(cell.getStringCellValue());
                                } catch (Exception e) {
                                    flag = 1;
                                }
                                break;
                            case 5:
                                try {
                                    p.setTransporterName(cell.getStringCellValue());
                                } catch (Exception e) {
                                    System.out.println("Name");
                                    flag = 1;
                                }
                                break;
                            case 6:
                                try {
                                    p.setLoadingPoint(cell.getStringCellValue());
                                } catch (Exception e) {
                                    System.out.println("LoadingPoint");
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
            List<Rates> products = ContractPriceUpload.convertExcelToListOfProduct(file.getInputStream());            
            this.contractpricerepo.saveAll(products);
            System.out.println("Saved");
        } catch (IOException e) {
            System.out.println("Error is error");
            e.printStackTrace();
        }
    }
}
