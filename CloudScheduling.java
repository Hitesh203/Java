package com.cloud;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class CloudScheduling {
	private static List<Cloudlet> cloudletList;
    public static void main(String[] args) {
        Log.printLine("Starting CloudSimVMAllocationExample...");
        try {
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;
            CloudSim.init(numUsers, calendar, traceFlag);
            Datacenter datacenter = createDatacenter("Datacenter_0");
            DatacenterBroker broker = new DatacenterBroker("Broker");
            List<Vm> vmList = new ArrayList<>();
            cloudletList = new ArrayList<>();
            Vm vm = new Vm(0, broker.getId(), 1000, 1, 512, 1000,
                    10000, "Xen", new CloudletSchedulerTimeShared());
            vmList.add(vm);
            broker.submitVmList(vmList);
            UtilizationModel utilizationModel = new UtilizationModelFull();
            Cloudlet cloudlet1 = new Cloudlet(0, 400000, 1, 300, 300, utilizationModel, utilizationModel, utilizationModel);
            cloudlet1.setUserId(broker.getId());
            cloudlet1.setVmId(0);
            cloudletList.add(cloudlet1);
            Cloudlet cloudlet2 = new Cloudlet(1, 200000, 1, 300, 300, utilizationModel, utilizationModel, utilizationModel);
            cloudlet2.setUserId(broker.getId());
            cloudlet2.setVmId(0);
            cloudletList.add(cloudlet2);
            broker.submitCloudletList(cloudletList);
            CloudSim.startSimulation();
            CloudSim.stopSimulation();
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            printCloudletList(newList);
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("An error occurred");
        }
    }

    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();
        peList.add(new Pe(0, new PeProvisionerSimple(3000)));
        int hostId = 0;
        int ram = 2048;
        long storage = 1000000;
        int bw = 10000;
        hostList.add(new Host(hostId, new
                RamProvisionerSimple(ram), new BwProvisionerSimple(bw), storage,
                peList, new VmSchedulerTimeShared(peList)));
        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double time_zone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        LinkedList<Storage> storageList = new LinkedList<>();
        DatacenterCharacteristics characteristics = new
                DatacenterCharacteristics(arch, os, vmm, hostList, time_zone,
                cost, costPerMem, costPerStorage, costPerBw);
        try {
            return new Datacenter(name, characteristics, new
                    VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) { 
            e.printStackTrace();
            return null;
        }
    }
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.printLine("Cloudlet ID: " + cloudlet.getCloudletId() + "\tSUCCESS");
            } else {
                Log.printLine("Cloudlet ID: " + cloudlet.getCloudletId() + "\tFAILED");
            }
        }
    }
}