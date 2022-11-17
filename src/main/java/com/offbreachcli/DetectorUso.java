package com.offbreachcli;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rafae
 */
public class DetectorUso {

    HardwareData hwData = new HardwareData();
    DatabaseConnection dbConnection = new DatabaseConnection();

    public void executar() {
        Integer currentValue = dbConnection.getServerDangerStatus();
        System.out.println(currentValue);
        Integer newValue = calculateUse(currentValue);
        System.out.println(newValue);
        if (isServerBeingHacked(currentValue)) {
            terminarProcesso();
            System.out.println("Terminou um processo");
        }
        dbConnection.saveServerDangerStatus(newValue);
    }
    
    private Boolean isServerBeingHacked(Integer index) {
        return index >= 200;
    }

    private Integer calculateUse(Integer currentIndex) {
        Double usoRamPercentage = ((double) hwData.getMemoryData().getEmUso() / hwData.getTotalMemoria()) * 100;
        Double usoCpuPercentage = Double.min(hwData.getProcessador().getUso() * 1.5, 100);
        Double usoDiscoPercentage = hwData.getTempoAtividadeDisco();

        currentIndex += calculateRisk(usoRamPercentage);
        currentIndex += calculateRisk(usoCpuPercentage);
        currentIndex += calculateRisk(usoDiscoPercentage);

        return Math.min(200, Math.max(currentIndex, 0));
    }

    private Integer calculateRisk(Double use) {
        if (use > 95) {
            return 15;
        } else if (use > 90) {
            return 10;
        } else if (use > 80) {
            return 5;
        } else if (use > 70) {
            return 1;
        } else if (use > 60) {
            return -5;
        } else {
            return -15;
        }
    }
    
    

    private void terminarProcesso() {
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec("ps aux --sort=-%mem");
            List<String> results;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String inputLine;
                results = new ArrayList<>();
                while ((inputLine = in.readLine()) != null) {
                    results.add(inputLine);
                }
            }
            Integer processPID = getProcessPID(results);
            String command2 = "kill -9 " + processPID;
            r.exec(command2);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private Integer getProcessPID(List<String> output) {
        String secondLine = output.get(1);
        if (secondLine.contains("jvm") || secondLine.contains("java")) {
            return formatOutputToGetPID(output, 2);
        } else {
            return formatOutputToGetPID(output, 1);
        }
    }

    private Integer formatOutputToGetPID(List<String> output, Integer index) {
        String[] listOfOcurrences = output.get(index).split(" ");
        for (int i = 1; i < listOfOcurrences.length; i++) {
            if (!listOfOcurrences[i].isEmpty()) {
                return Integer.valueOf(listOfOcurrences[i]);
            }
        }
        return 0;
    }
}
