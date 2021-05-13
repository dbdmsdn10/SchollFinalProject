package com.example.schoolfinalproject;

public class AlarmInfo {
    String startTime,endTime,cycleTime,breakfirst,lunch,dinner,drugtime;
    int drug=-1,drugname=-1,drugresult=-1;//drugname은 첫번째 스피너,drug는 2번째 스피너, result는 어떻게 할지, -1이면 없음,0이면 개인 문자시간,1은 30분전, 2는 15분전, 3은 직후(1시간후)

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(String cycleTime) {
        this.cycleTime = cycleTime;
    }

    public String getBreakfirst() {
        return breakfirst;
    }

    public void setBreakfirst(String breakfirst) {
        this.breakfirst = breakfirst;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }

    public int getDrug() {
        return drug;
    }

    public void setDrug(int drug) {
        this.drug = drug;
    }

    public String getDrugtime() {
        return drugtime;
    }

    public void setDrugtime(String drugtime) {
        this.drugtime = drugtime;
    }

    public int getDrugresult() {
        return drugresult;
    }

    public void setDrugresult(int drugresult) {
        this.drugresult = drugresult;
    }

    public int getDrugname() {
        return drugname;
    }

    public void setDrugname(int drugname) {
        this.drugname = drugname;
    }
}
