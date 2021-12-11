package com.xuchen.crawler;

import com.xuchen.mapper.CommodityMapper;
import com.xuchen.pipeline.SqlPipeline;
import com.xuchen.pojo.Commodity;
import com.xuchen.processor.EbayPageProcessor;
import com.xuchen.scheduler.NumberScheduler;
import com.xuchen.scheduler.TimeScheduler;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.Scheduler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

public class MyFrame extends JFrame {
    public Spider spider = null;

    // Default value of configuration
    // You also need to modify the value in resetConfig() if you want to change the default
    String keyword = "fan";
    int numberOfPages = 40;
    int crawlTime = 10;
    int numberOfThreads = 5;
    String resource = "mybatis-config.xml";

    boolean useSqlPipeline = true;
    Pipeline pipeline = null;
    boolean useNumberScheduler = true;
    Scheduler scheduler = null;

    JLabel keywordLabel = new JLabel("Type your keyword here: (default: fan)");
    JTextField keywordField = new JTextField(20);
    JLabel numberOfPagesLabel = new JLabel("Type number of pages you want to search: (default: "+ numberOfPages +" pages)");
    JTextField numberOfPagesField = new JTextField(String.valueOf(numberOfPages),20);
    JLabel timeLabel = new JLabel("Time limit: (default: "+String.valueOf(crawlTime)+"s)");
    JTextField timeField = new JTextField(String.valueOf(crawlTime),20);
    JLabel numberOfThreadLabel = new JLabel("Number of thread to be used: (default: "+ numberOfThreads +")");
    JTextField numberOfThreadField = new JTextField(String.valueOf(numberOfThreads),20);
    JLabel outputLabel = new JLabel("Which output would you like?");
    JRadioButton rb1 = new JRadioButton("To console");
    JRadioButton rb2 = new JRadioButton("Save to database (default)", true);
    ButtonGroup rbGroup1 = new ButtonGroup();
    JLabel schedulerLabel = new JLabel("What kind of limitation would you like?");
    JRadioButton rb3 = new JRadioButton("Number of Pages (default)",true);
    JRadioButton rb4 = new JRadioButton("Time limit");
    ButtonGroup rbGroup2 = new ButtonGroup();
    JLabel reminderLabel = new JLabel("Reminder:");
    JTextArea reminder = new JTextArea(2,25);
    JTextArea log;
    JButton clear;
//    JTextField outputField = new JTextField(20);

    public MyFrame() {
        setLayout(new BorderLayout());
        setTitle("Multi-thread Crawler");
        setSize(1200,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        JButton run = new JButton("Run");
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                run();
            }
        });
        mainPanel.add(run);
        run.setBounds(40,400,120,40);
        mainPanel.add(keywordLabel);
        keywordLabel.setBounds(200,20,300,25);
        mainPanel.add(keywordField);
        keywordField.setBounds(200,40,300,25);
        mainPanel.add(numberOfPagesLabel);
        numberOfPagesLabel.setBounds(200,90,400,25);
        mainPanel.add(numberOfPagesField);
        numberOfPagesField.setBounds(200,110,300,25);
        mainPanel.add(timeLabel);
        timeLabel.setBounds(200,160,300,25);
        mainPanel.add(timeField);
        timeField.setBounds(200,180,300,25);
        mainPanel.add(numberOfThreadLabel);
        numberOfThreadLabel.setBounds(200,230,300,25);
        mainPanel.add(numberOfThreadField);
        numberOfThreadField.setBounds(200,250,300,25);
        mainPanel.add(outputLabel);
        outputLabel.setBounds(200,300,300,25);

        rbGroup1.add(rb1);
        rbGroup1.add(rb2);
        rbGroup2.add(rb3);
        rbGroup2.add(rb4);
        mainPanel.add(rb1);
        rb1.setBounds(200,320,200,25);
        mainPanel.add(rb2);
        rb2.setBounds(200,345,200,25);
        mainPanel.add(schedulerLabel);
        schedulerLabel.setBounds(200,370,300,25);
        mainPanel.add(rb3);
        rb3.setBounds(200,390,200,25);
        mainPanel.add(rb4);
        rb4.setBounds(200,415,200,25);
        mainPanel.add(reminderLabel);
        reminderLabel.setBounds(200,450,200,25);
        mainPanel.add(reminder);
        reminder.setBounds(200,475,350,55);
        reminder.setText("(You can set the params above.)");
        JButton showParams = new JButton("Check Params");
        showParams.addActionListener(e -> config());
        mainPanel.add(showParams);
        showParams.setBounds(40,250,120,40);
        JButton reset = new JButton("Reset Params");
        reset.addActionListener(e -> resetConfig());
        mainPanel.add(reset);
        reset.setBounds(40,100,120,40);
        // TextArea
        log = new JTextArea("Statistic data and database will be displayed here",10,40);
        log.setLineWrap(true);
        JScrollPane logJsp = new JScrollPane(log);
        mainPanel.add(logJsp);
        logJsp.setBounds(570,50,550,350);
        JButton fetch = new JButton("Fetch DB");
        fetch.addActionListener(e -> seeDatabase());
        mainPanel.add(fetch);
        fetch.setBounds(700,420,120,40);
        clear = new JButton("Clear DB");
        clear.addActionListener(e -> clearDatabase());
        mainPanel.add(clear);
        clear.setBounds(850,420,120,40);
        JLabel beginner = new JLabel("You can just click RUN!");
        mainPanel.add(beginner);
        beginner.setBounds(20,5,200,25);
        this.add(mainPanel);
    }
    // Action of run button
    public void run() {
        // Connect to MySQL
        config();
        SqlSession sqlSession = initiateSqlSession();
        // Get a object via interface
        CommodityMapper commodityMapper = sqlSession.getMapper(CommodityMapper.class);
        // Configure the Spider
        if (useSqlPipeline) {
            pipeline = new SqlPipeline(commodityMapper);
        } else {
            pipeline = new ConsolePipeline();
        }
        if (useNumberScheduler) {
            setPageSpider(keyword, pipeline, numberOfThreads, numberOfPages);
        } else {
            setTimeSpider(keyword, pipeline, numberOfThreads, crawlTime);
        }

        int sizeBefore = fetchDatabase().size();
        long time1 = new Date().getTime();
        spider.run();
        long time2 = new Date().getTime();
        // Log output
        if (pipeline instanceof SqlPipeline){
            SqlPipeline sqlPipeline = (SqlPipeline)pipeline;
            int numberOfItems = sqlPipeline.getProcessTimes();
            System.out.println( numberOfItems + " items are collected. They are saved in database.");
        }
        long timeConsume = time2 - time1;
        log.setText("Finish using keyword "+keyword+" and search in Ebay website.");
        log.append("\nThe total crawling time is " + timeConsume + "ms.");
        if (scheduler instanceof TimeScheduler){
            int numberOfVisit = ((TimeScheduler) scheduler).getNumberOfVisit();
            log.append("\nNumber of visited pages is "+numberOfVisit);
        }
        if(useSqlPipeline) {
            log.append("\nData is just saved in the database.");
        }
        int sizeAfter = fetchDatabase().size();
        int increment = sizeAfter - sizeBefore;
        log.append("\n"+increment+" items are added just now!");
        log.append("\nThere are "+sizeAfter+" items in the database.");
        log.append("\nClick \"Fetch DB\" to see details.");
        log.append("\n\n(More information is available in the console)");

        sqlSession.close();
    }

    // Remind user number is too small
    public void numberTooSmall(){
        reminder.setText("Number is too small! Params change failed.");

    }

    // Remind user there is an invalid input number
    public void invalidInput(){
        reminder.setText("Invalid input! Params change failed.");
    }

    public void config(){
        String s1;
        int s2;
        int s3;
        int s4;
        boolean s5;
        boolean s6;
        try {
            s1 = keywordField.getText();
            s2 = Integer.parseInt(numberOfPagesField.getText());
            s3 = Integer.parseInt(timeField.getText());
            s4 = Integer.parseInt(numberOfThreadField.getText());
            s5 = rb2.isSelected();
            s6 = rb3.isSelected();

            if (s2 < 1 || s3 < 1 || s4 < 1){
                numberTooSmall();
            }
            else {
                if (s1 != null && !(s1.equals(""))){
                    keyword = s1;
                } else {
                    keyword = "fan";
                }
                numberOfPages = s2;
                crawlTime = s3;
                numberOfThreads = s4;
                useSqlPipeline = s5;
                useNumberScheduler = s6;
                reminder.setText("Success! ");
                reminder.append("keyword="+keyword+", Pages="+numberOfPages+", Time limit="+crawlTime+", Threads="+numberOfThreads+"\n"+", Save to database="+useSqlPipeline+", Pages limit="+useNumberScheduler);
            }
        } catch (NumberFormatException e) {
            invalidInput();
        }
    }

    public void resetConfig(){
        keywordField.setText("");
        numberOfPagesField.setText("40");
        timeField.setText("10");
        numberOfThreadField.setText("5");
        rb2.setSelected(true);
        rb3.setSelected(true);
        reminder.setText("All params are reset!");
    }

    public void setPageSpider(String keyword, Pipeline pipeline, int numberofThreads, int numberOfPages) {
        String url = "https://www.ebay.com/sch/i.html?_from=R40&_trksid=p2380057.m570.l1313&_nkw="+keyword+"&_sacat=0";
        spider = Spider.create(new EbayPageProcessor())
                .addUrl(url)
                .addPipeline(pipeline)
                .thread(numberofThreads)
                .setScheduler(new NumberScheduler(numberOfPages));
    }

    public void setTimeSpider(String keyword, Pipeline pipeline, int numberofThreads, int time) {
        String url = "https://www.ebay.com/sch/i.html?_from=R40&_trksid=p2380057.m570.l1313&_nkw="+keyword+"&_sacat=0";
        scheduler = new TimeScheduler(time);
        spider = Spider.create(new EbayPageProcessor())
                .addUrl(url)
                .addPipeline(pipeline)
                .thread(numberofThreads)
                .setScheduler(scheduler);
    }

    public void seeDatabase(){
        List<Commodity> results = fetchDatabase();
        if (results.size()==0){
            log.setText("The table in database is empty! Try some Crawling now.");
        }else{
            log.setText("There are "+results.size()+" items in total.");
            int number = 1;
            for (Commodity result:results) {
                log.append("\nNo."+number+":");
                log.append("\n"+result.toString());
                number += 1;
            }
            log.append("\n\nThere are "+results.size()+" items in total.");
        }

    }

    public List<Commodity> fetchDatabase() {
        SqlSession sqlSession = initiateSqlSession();
        // Get a object via interface
        CommodityMapper commodityMapper = sqlSession.getMapper(CommodityMapper.class);
        java.util.List<Commodity> results = commodityMapper.selectAll();

        sqlSession.commit();
        sqlSession.close();
        return results;
    }

    public void clearDatabase() {
        int option = JOptionPane.showConfirmDialog(clear,"Confirm to clear","Reminder",0);
        if(option == 0){
            SqlSession sqlSession = initiateSqlSession();
            // Get a object via interface
            CommodityMapper commodityMapper = sqlSession.getMapper(CommodityMapper.class);
            commodityMapper.clearAll();

            sqlSession.commit();
            sqlSession.close();
            log.setText("Database is clear now!");
        }

    }

    public SqlSession initiateSqlSession() {

        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        return sqlSessionFactory.openSession(true);

    }

    public static void main(String[] args) {

        MyFrame frame = new MyFrame();
        frame.setVisible(true);

    }


    // PageProcessor: TestPageProcessor, Pipeline: console, without GUI
    @Test
    public void testCrawl(){
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        // Get a object via interface
        CommodityMapper commodityMapper = sqlSession.getMapper(CommodityMapper.class);

        // Configuration
        String name = "fan";
        int numberOfPages = 20;
        Pipeline pipeline = new SqlPipeline(commodityMapper);
//        pipeline = new ConsolePipeline();
        String url = "https://www.ebay.com/sch/i.html?_from=R40&_trksid=p2380057.m570.l1313&_nkw="+name+"&_sacat=0";
        long time1 = new Date().getTime();
        Spider.create(new EbayPageProcessor())
                .addUrl(url)
                .addPipeline(pipeline)
                .thread(5)
                .setScheduler(new NumberScheduler(numberOfPages))
                .run();
        long time2 = new Date().getTime();
        // Log
//        SqlPipeline sqlPipeline = (SqlPipeline)pipeline;
//        int numberOfItems = sqlPipeline.getProcessTimes();
//        System.out.println( numberOfItems + " items are collected. They are saved in database.");
        long timeConsume = time2 - time1;
        System.out.println("The total crawling time is " + timeConsume + "ms.");
        sqlSession.close();
    }

    @Test
    public void clearTable(){
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        // Get a object via interface
        CommodityMapper commodityMapper = sqlSession.getMapper(CommodityMapper.class);
        commodityMapper.clearAll();

        sqlSession.commit();
        sqlSession.close();
    }

    @Test
    public void selectAll() {
        String resource = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession(true);
        // Get a object via interface
        CommodityMapper commodityMapper = sqlSession.getMapper(CommodityMapper.class);
        java.util.List<Commodity> results = commodityMapper.selectAll();
        for (Commodity result:results) {
            System.out.println(result);
        }
        sqlSession.commit();
        sqlSession.close();
    }
}
