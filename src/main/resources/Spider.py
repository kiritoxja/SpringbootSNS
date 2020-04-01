#!/usr/bin/env python
# -*- encoding: utf-8 -*-
# Created on 2019-02-21 19:38:47
# Project: v2ex

from pyspider.libs.base_handler import *
import pymysql
import random

class Handler(BaseHandler):
    crawl_config = {
    }

    def __init__(self):
        # 数据库连接所需参数
        self.db = pymysql.connect(user='root', passwd='199661', host='localhost', port=3306, db='sns', charset='utf8')
    
    def add_question(self, title, content):
        try:
            cursor = self.db.cursor()
            sql = 'insert into question(title, content, user_id, created_date, comment_count) values ("%s" ,"%s" ,%d, now(), 0)' % (title, content, random.randint(2,11))
            cursor.execute(sql)
            #插入问题的主键
            print(cursor.lastrowid)
            self.db.commit()
        except Exception as e:
            print(e)
            self.db.rollback()

    #每天执行一次调度器
    @every(minutes=24 * 60)
    def on_start(self):
        self.crawl('https://www.v2ex.com', callback=self.index_page, validate_cert=False)

    @config(age=10 * 24 * 60 * 60) # 这表示我们认为 10 天内页面有效,不会再次进行更新抓取
    #每个问题的链接以/t开头 每个topic的class都是topic-link
    #选出所有/t/655571#reply20这种链接   获得每个topic的url
    def index_page(self, response):
            for each in response.doc('a.topic-link').items():
                url = each.attr.href
                if url.find('#reply') > 0:
                    url = url[0:url.find('#')]
                #站内相对地址跳转不用再新加了 pyspider会自动加的
                self.crawl(url, callback=self.detail_page, validate_cert=False)

    #每个话题的详情处理
    @config(priority=2)
    def detail_page(self, response):
        title = response.doc('h1').text()
        if response.doc('div.topic_content>div>p').text() == "":
            content = response.doc('div.topic_content').text()
        else :
            content = response.doc('div.topic_content>div>p').text()
        self.add_question(title,content)
        return {
            "url": response.url,
            "title": response.doc('title').text(),
        }