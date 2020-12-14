/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.designer.kiosko.reportes;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

/**
 *
 * @author usuario
 */
@Stateless
public class IniciarReporte {

    private Connection inicarC(EntityManager em) {
        Connection conexion = null;
        try {
            conexion = em.unwrap(java.sql.Connection.class);
        } catch (Exception e) {
            System.out.println("Error: " + this.getClass().getName() + ".iniciarC() " + "Causa: " + e);
        }
        return conexion;
    }

    public String ejecutarReporte(String nombreReporte, String rutaReporte, String rutaGenerado, String nombreArchivo, String tipoReporte, Map parametros, EntityManager em) {
        Connection conexion = null;
        try {
            File archivo = new File(rutaReporte + nombreReporte + ".jasper");
            parametros.put("pathImagenes", rutaReporte);
            conexion = inicarC(em);
            JasperReport masterReport;
            masterReport = (JasperReport) JRLoader.loadObject(archivo);
            JasperPrint imprimir = JasperFillManager.fillReport(masterReport, parametros, conexion);
            String outFileName = rutaGenerado + nombreArchivo;
            Exporter exporter = null;
            if (tipoReporte.equals("PDF")) {
                exporter = new JRPdfExporter();
            }
            if (tipoReporte.equals("XLS")) {
                exporter = new JRXlsExporter();
            }

            if (exporter != null) {
                List<JasperPrint> jpl = new ArrayList<>();
                jpl.add(imprimir);
                exporter.setExporterInput(SimpleExporterInput.getInstance(jpl));
                exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outFileName));
                exporter.exportReport();
            }
            return outFileName;
        } catch (JRException e) {
            System.out.println("Error: IniciarReporte.ejecutarReporte: " + e);
            System.out.println("************************************");
            if (e.getCause() != null) {
                return "Error: INICIARREPORTE " + e.toString() + "\n" + e.getCause().toString();
            } else {
                return "Error: INICIARREPORTE " + e.toString();
            }
        }
    }
}
