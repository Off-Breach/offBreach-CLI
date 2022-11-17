package com.offbreachcli;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DatabaseConnection {

    SqlServerConnection remoteConnection = new SqlServerConnection();
    JdbcTemplate sqlServerConnection = remoteConnection.getDataSource();
    SqlLocalConnection localConnection = new SqlLocalConnection();
    JdbcTemplate sqlLocalConnection = localConnection.getDataSource();
    HardwareData hwData = new HardwareData();
    private String emailFuncionario;
    private String senhaFuncionario;
    public String fkClinica = "1";
    public String fkServidor = "2";

    public void setConnection(String email, String senha) {
        this.emailFuncionario = email;
        this.senhaFuncionario = senha;
    }

    public String getEmail() {
        String result = "";
        String select = String.format("SELECT email FROM Funcionario "
                + "WHERE email = '%s' AND senha = '%s'",
                this.emailFuncionario, this.senhaFuncionario);
        try {
            result = sqlServerConnection.queryForObject(select, String.class);
            log.info("Email cadastrado");
        } catch (EmptyResultDataAccessException e) {
            log.error("Email não cadastrado");
        }
        return result;
    }

    public String getSenha() {
        String result = null;
        String select = String.format("SELECT senha FROM funcionario "
                + "WHERE email = '%s' AND senha = '%s'",
                this.emailFuncionario, this.senhaFuncionario);
        try {
            result = sqlServerConnection.queryForObject(select, String.class);
            log.info("Senha correta!");
        } catch (EmptyResultDataAccessException e) {
            log.error("Senha incorreta!");

        }
        return result;
    }

    public String getNome() {
        String result = null;
        String select = String.format("SELECT nomeFuncionario FROM funcionario "
                + "WHERE email = '%s'",
                this.emailFuncionario);

        try {
            result = sqlServerConnection.queryForObject(select, String.class);
            log.info("Nome encontrado");
        } catch (EmptyResultDataAccessException e) {
            log.error("Nome não encontrado!");
        }
        return result;
    }

    public String getFkClinica() {
        String result = null;
        String select = String.format(""
                + "SELECT fkClinica FROM funcionario "
                + "WHERE email = '%s'",
                this.emailFuncionario);
        try {
            result = sqlServerConnection.queryForObject(select, String.class);
            this.fkClinica = result;
            log.info("Cadastrado a uma clínica");
        } catch (EmptyResultDataAccessException e) {
            log.error("Não cadastrado a uma clínica");
        }
        return result;
    }

    public void createLocalDatabase() {
        String query = ""
                + "CREATE DATABASE offbreach;\n"
                + "USE offbreach;\n"
                + "CREATE TABLE dadosRam (\n"
                + "idDadoRam INT PRIMARY KEY AUTO_INCREMENT,\n"
                + "uso FLOAT,\n"
                + "dtDado DATETIME,\n"
                + "fkRam INT\n"
                + ");\n"
                + "CREATE TABLE dadosCpu (\n"
                + "idDadoCpu INT PRIMARY KEY AUTO_INCREMENT,\n"
                + "temperatura FLOAT,\n"
                + "uso FLOAT,\n"
                + "dtDado DATETIME,\n"
                + "fkCpu INT\n"
                + ");\n"
                + "CREATE TABLE dadosDisco (\n"
                + "idDadoDisco INT PRIMARY KEY AUTO_INCREMENT,\n"
                + "uso LONG,\n"
                + "dtDado DATETIME,\n"
                + "fkDisco INT\n"
                + ");";
        try {
            sqlLocalConnection.update(query);
        } catch (DataAccessException e) {

        }
    }

    public Boolean verifyHostname() {
        String hostname = hwData.getHostname();
        Boolean result;
        String select = String.format("SELECT idServidor FROM servidor "
                + "WHERE hostname = '%s' AND fkClinica = %s",
                hostname, fkClinica);
        try {
            sqlServerConnection.queryForObject(select, String.class);
            result = false;
            log.info("Hostname encontrado");
        } catch (EmptyResultDataAccessException exception) {
            result = true;
            log.error("Hostname não encontrado");
        }
        return result;
    }

    public String getMachineId() {
        hwData.setHostname();
        String hostname = hwData.getHostname();
        String result = null;
        String select = String.format("SELECT idServidor FROM servidor "
                + "WHERE hostname = '%s' AND fkClinica = %s",
                hostname, this.fkClinica);
        try {
            result = sqlServerConnection.queryForObject(select, String.class);
            fkServidor = result;
            log.info("Máquina encontrada com sucesso!");
        } catch (EmptyResultDataAccessException exception) {
            saveHardwareData();
            log.error("Máquina não encontrada!");
        }
        return result;
    }

    public String getServerPublicName() {
        String idServidor = getMachineId();
        String result;
        String select = String.format("SELECT popularName FROM servidor "
                + "WHERE idServidor = %s",
                idServidor);
        try {
            result = sqlServerConnection.queryForObject(select, String.class);
        } catch (DataAccessException e) {
            result = hwData.getHostname();
        }
        return result;
    }

    public Integer getServerDangerStatus() {
        String idServidor = getMachineId();
        String result;
        String query = String.format(""
                + "SELECT statusPerigo FROM servidor "
                + "WHERE idServidor = %s",
                idServidor);
        System.out.println(query);
        try {
            result = sqlServerConnection.queryForObject(query, String.class);
        } catch (DataAccessException e) {
            log.error("\nErro ao conseguir o status de perigo da máquina\n");
            result = "0";
        }
        return Integer.valueOf(result);
    }

    public void saveServerDangerStatus(Integer status) {
        String idServidor = getMachineId();
        String query = String.format(""
                + "UPDATE servidor set statusPerigo = %s WHERE idServidor = %s",
                status, idServidor);
        try {
            sqlServerConnection.update(query);
        } catch (DataAccessException e) {
            log.error("\n Erro ao inserir statusPeigo\n");
        }
    }

    public void saveHardwareData() {
        hwData.setHostname();
        String hostname = hwData.getHostname();
        String sistema = hwData.getSistema().getSistemaOperacional();

        String insert = String.format("INSERT INTO servidor (hostName, sistemaOperacional, popularName, fkClinica)"
                + "VALUES ('%s', '%s', '%s', %s)",
                hostname, sistema, hostname, fkClinica);
        if (this.verifyHostname()) {
            try {
                sqlServerConnection.update(insert);
                log.info("\nMáquina inserida com sucesso\n");
            } catch (DataAccessException error) {
                log.error("Erro ao inserir máquina no banco");
            }
        } else {
            log.warn("\nMáquina já cadastrada\n");
        }
    }

    public void saveRamFixedData() {
        String insertRamFixedData = String.format("INSERT INTO ram (fkServidor, qtdRamTotal) "
                + "VALUES (%s, %s)",
                fkServidor, hwData.getTotalMemoria()
        );
        try {
            sqlServerConnection.update(insertRamFixedData);
        } catch (DataAccessException e) {
            log.error("\nErro ao inserir dados no banco - saveRamFixedData\n");
        }
    }

    public void saveCpuFixedData() {
        String query = String.format("INSERT INTO cpu (nomeCpu, fkServidor)"
                + "VALUES ('%s', %s)",
                hwData.getProcessador().getNome(), fkServidor
        );
        try {
            sqlServerConnection.update(query);
        } catch (DataAccessException e) {
            log.error("\nErro ao inserir dados no banco - saveCpuFixedData\n");
        }
    }

    public void saveDiskFixedData() {
        String query = String.format("INSERT INTO disco (nomeDisco, tamanho, fkServidor)"
                + "VALUES ('%s', %s, %s)",
                hwData.getDiscoNome(0), hwData.getTotalDisco(), fkServidor
        );
        try {
            sqlServerConnection.update(query);
        } catch (DataAccessException e) {
            log.error("\nErro ao inserir dados no banco - saveCpuFixedData\n");
        }
    }

    public String getRamId() {
        String result = null;
        String query = String.format(""
                + "SELECT idRam FROM ram "
                + "JOIN servidor ON fkServidor = idServidor "
                + "WHERE fkServidor = %s AND hostName = '%s'",
                getMachineId(), hwData.getHostname());
        try {
            result = sqlServerConnection.queryForObject(query, String.class);
        } catch (DataAccessException e) {
            saveRamFixedData();
            log.error("\nErro ao conseguir os dados do banco - getRamId()\n");
        }
        return result;
    }

    public String getCpuId() {
        String result = null;
        String query = String.format(""
                + "SELECT idCpu FROM cpu "
                + "JOIN servidor ON fkServidor = idServidor "
                + "WHERE fkServidor = %s AND hostName = '%s'",
                getMachineId(), hwData.getHostname());
        try {
            result = sqlServerConnection.queryForObject(query, String.class);
        } catch (DataAccessException e) {
            saveCpuFixedData();
            log.error("\nErro ao conseguir os dados do banco - getCpuId()\n");
        }
        return result;
    }

    public String getDiskId() {
        String result = null;
        String query = String.format(""
                + "SELECT idDisco FROM disco "
                + "JOIN servidor ON fkServidor = idServidor "
                + "WHERE fkServidor = %s AND hostName = '%s'",
                getMachineId(), hwData.getHostname()
        );
        try {
            result = sqlServerConnection.queryForObject(query, String.class);
            log.info("Disco encontrado com sucesso");
        } catch (DataAccessException e) {
            saveDiskFixedData();
            log.error("Disco não encontrado");
        }
        return result;
    }

    public void saveDataInLoop() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();

        Double temperatura = hwData.getTemperatura();
        Double usoCpu = hwData.getProcessador().getUso();
        String usoRam = hwData.getMemoryData().getEmUso().toString();
        Double tempoAtividadeDisco = hwData.getTempoAtividadeDisco();
        Double usoDisk = hwData.getUsoDisco();
        String hora = formatter.format(date);
        String fkRam = getRamId();
        String fkCpu = getCpuId();
        String fkDisk = getDiskId();

        String insertCpu = String.format("INSERT INTO DadosCpu(fkCpu, temperatura, uso, dtDado)"
                + "VALUES (%s, %s, %s, '%s')",
                fkCpu, temperatura, usoCpu, hora);

        String insertMemory = String.format("INSERT INTO DadosRam(fkRam, uso, dtDado) "
                + "VALUES (%s, %s, '%s')",
                fkRam, usoRam, hora);

        String insertDisk = String.format("INSERT INTO DadosDisco(fkDisco, uso, tempoAtividade, dtDado) "
                + "VALUES (%s, %s, %s, '%s')",
                fkDisk, usoDisk, tempoAtividadeDisco, hora);
        try {
            sqlServerConnection.update(insertCpu);
            sqlServerConnection.update(insertMemory);
            sqlServerConnection.update(insertDisk);
            log.info("\nDados inseridos com sucesso\n");
        } catch (DataAccessException error) {
            log.error("\nErro ao inserir dados no banco\n");
            log.error(error.getMessage());
        }
    }
}