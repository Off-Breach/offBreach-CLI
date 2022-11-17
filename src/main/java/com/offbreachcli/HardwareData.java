package com.offbreachcli;


import com.github.britooo.looca.api.core.Looca;
import com.github.britooo.looca.api.group.discos.Disco;
import com.github.britooo.looca.api.group.discos.DiscosGroup;
import com.github.britooo.looca.api.group.discos.Volume;
import com.github.britooo.looca.api.group.memoria.Memoria;
import com.github.britooo.looca.api.group.processador.Processador;
import com.github.britooo.looca.api.group.sistema.Sistema;
import com.github.britooo.looca.api.group.temperatura.Temperatura;
import com.github.britooo.looca.api.util.Conversor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;
import oshi.util.Util;

public class HardwareData {

    private final Looca looca = new Looca();
    private final Sistema sistema = looca.getSistema();
    private String hostname = "";
    private final DiscosGroup discos = looca.getGrupoDeDiscos();
    private final Memoria memoria = looca.getMemoria();

    public void cadastrarSistema() {

        Temperatura temperatura = looca.getTemperatura();
        Processador processador = looca.getProcessador();
    }

    public Memoria getMemoryData() {
        return looca.getMemoria();
    }

    public String getMemoriaEmUso() {
        return Conversor.formatarBytes(getMemoryData().getEmUso());
    }

    public Long getTotalMemoria() {
        return memoria.getTotal();
    }

    public List<Disco> getDiscoData() {
        return discos.getDiscos();
    }

    public Long getTotalDisco() {
        return discos.getTamanhoTotal();
    }

    public Double getTempoAtividadeDisco() {
        HWDiskStore disk = new SystemInfo().getHardware().getDiskStores().get(0);
        Long firstGetTransferTime = disk.getTransferTime();
        Long firstGetTimeStamp = disk.getTimeStamp();
        Util.sleep(5000);
        disk.updateAttributes();
        Long secondGetTransferTime = disk.getTransferTime();
        Long secondGetTimeStamp = disk.getTimeStamp();
        return (double) (secondGetTransferTime - firstGetTransferTime) / (secondGetTimeStamp - firstGetTimeStamp) * 100;
    }

    public Long getDisponivelDisco() {
        return discos.getVolumes().get(0).getDisponivel();
    }
    
    public Double getUsoDisco() {
        return (double) getTotalDisco() - getDisponivelDisco();
    }

    public String getDiscoNome(Integer index) {
        Disco disco = getDiscoData().get(index);
        return disco.getNome();
    }

    public Integer getQuantidadeDeDiscos() {
        return getDiscoData().size();
    }

    public Double getTemperatura() {
        return looca.getTemperatura().getTemperatura();
    }

    public Processador getProcessador() {
        return looca.getProcessador();
    }

    public Sistema getSistema() {
        return looca.getSistema();
    }

    public String getHostname() {
        return this.hostname;
    }

    public void setHostname() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            this.hostname = address.getHostName();
        } catch (UnknownHostException e) {
            System.out.println("[Erro] - Não foi possível obter o hostname");
            this.hostname = "hostname";
        }
    }
}
