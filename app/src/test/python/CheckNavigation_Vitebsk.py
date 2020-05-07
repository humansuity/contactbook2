#!/usr/bin/python3
# -*- coding: utf-8 -*-
# This sample code uses the Appium python client
# pip install Appium-Python-Client
# Then you can paste this into a file and simply run with Python
""" Test for Miriada Copyright 2020 """

import unittest
import time
from appium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.common.touch_actions import TouchActions
from appium.webdriver.common.multi_action import MultiAction
from selenium.common.exceptions import JavascriptException


__author__ = "Zhenya Zotov"

class ContackBookAppTestUpdateDBAppium(unittest.TestCase):
    """ Класс тестирования костакта книги: Обновление БД"""

    # UiAutomator2
    @classmethod
    def setUpClass(cls):
        # Делаем необходимые настройки, данные приложения обнуляются
        desired_caps = {
            'platformName': 'android',
            #'deviceName': 'emulator-5554',
            'deviceName': 'Galaxy_Nexus_API_28',
            'appActivity': 'net.gas.gascontact.ui.activities.SplashActivity',
            'appPackage': 'net.gas.gascontact',
            'automationName': 'UiAutomator2',
            'unicodeKeyboard': True,
            'resetKeyboard': True,
            'noReset': 'true',
            'full-reset': 'false'
        }
        cls.driver = webdriver.Remote("http://localhost:4723/wd/hub", desired_caps)

    def test_enter(self):
        """ Тестирование обновлений БД (для ГПО)"""
        time.sleep(4)

        #1. Переход во вкладку ПРУП "Витебскоблгаз"
        self.driver.find_element_by_id("net.gas.gascontact:id/fragmentHolder")
        elements = self.driver.find_elements_by_id("net.gas.gascontact:id/txt")

        #1.1. Поиск элемента ПРУП "Витебскоблгаз"
        for element in elements:
            if element.text == 'ПРУП  "Витебскоблгаз"':
                element.click()
                time.sleep(2)
                break

        #2. Выбор филиала ПУ"Витебскгаз"
        self.driver.find_element_by_id("net.gas.gascontact:id/fragmentHolder")
        elementsFilials = self.driver.find_elements_by_id("net.gas.gascontact:id/txt")

        #2.2. Поиск элемента в кладке Отделы
        for element in elementsFilials:
            if element.text == 'ПУ "Витебскгаз"':
                element.click()
                time.sleep(2)
                break

        #3. Выбор отдела "Упарвление ..."
        self.driver.find_element_by_id("net.gas.gascontact:id/fragmentHolder")
        elementsDepartament = self.driver.find_elements_by_id("net.gas.gascontact:id/txt")

        #3.1. Поиск элемента в кладке Отделы
        for element in elementsDepartament:
            if element.text == "Управление":
                element.click()
                time.sleep(2)
                break

        #4. Выбор сотрудника
        self.driver.find_element_by_id("net.gas.gascontact:id/recyclerView")

        #4.1. Делаем поиск по должности. В данном тесте должность "Ведущий инженер".
        elementTextPost = self.driver.find_elements_by_id("net.gas.gascontact:id/text_post")

        for element in elementTextPost:
            if element.text == "Главный инженер филиала":
                element.click()
                time.sleep(2)
                break

        #5. Поиск элементов: Мобильный телефон
        numberEmploeyr = self.driver.find_element_by_id("net.gas.gascontact:id/text_mobile")
        bntBack = self.driver.find_element_by_class_name("android.widget.ImageButton")

        if numberEmploeyr.text != "":
            #Проверка относится к 5 и 4 пункту.
            bntBack.click()
            time.sleep(2)

        #6. Проверки кнопки назад
        elmTextView = self.driver.find_element_by_class_name("android.widget.TextView")
        elmImageButton = self.driver.find_element_by_class_name("android.widget.ImageButton")


        while elmTextView.text != "Филиалы":
            bntBack.click()
            time.sleep(2)
            if elmTextView.text == "Филиалы":
                elmTxt = self.driver.find_element_by_id("net.gas.gascontact:id/txt")
                if elmTxt.text == 'ПУ "Витебскгаз"':
                    bntBack.click()
                    time.sleep(2)
                else:
                    break

        time.sleep(5)

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()

if __name__ == "__main__":
    SUITE = unittest.TestLoader().loadTestsFromTestCase(ContackBookAppTestUpdateDBAppium)
    unittest.TextTestRunner(verbosity=2).run(SUITE)
