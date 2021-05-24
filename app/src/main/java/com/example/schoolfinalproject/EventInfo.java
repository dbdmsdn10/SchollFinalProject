package com.example.schoolfinalproject;

public class EventInfo
{
    String id;
    String eventTitle;
    String eventContent;
    String eventStart;
    String eventEnd;

    public void setEventTitle(String title)
    {
        this.eventTitle = title;
    }

    public void setEventContent(String content)
    {
        this.eventContent = content;
    }

    public void setEventStart(String start)
    {
        this.eventStart = start;
    }

    public void setEventEnd(String end)
    {
        this.eventEnd = end;
    }

    public String getEventTitle()
    {
        return this.eventTitle;
    }

    public String getEventContent()
    {
        return this.eventContent;
    }

    public String getEventStart()
    {
        return this.eventStart;
    }

    public String getEventEnd()
    {
        return this.eventEnd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
