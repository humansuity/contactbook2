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
            'appActivity': 'net.gas.gascontact.view.ui.activities.SplashActivity',
            'appPackage': 'net.gas.gascontact',
            'automationName': 'UiAutomator2',
            'unicodeKeyboard': True,
            'resetKeyboard': True,
            'noReset': 'true',
            'full-reset': 'false'
        }
        cls.driver = webdriver.Remote("http://localhost:4723/wd/hub", desired_caps)

    def test_enter(self):
        """ Тестирование: Кнопка поиска и реализация самого поиска"""
        time.sleep(4)
        # нажание на кнопку FloatingActionButton
        btnFloatingActionButton = self.driver.find_element_by_id("net.gas.gascontact:id/floatingActionButton")
        btnFloatingActionButton.click()
        time.sleep(1)

        # нажание на кнопку SearchView
        btnSearchView = self.driver.find_element_by_id("net.gas.gascontact:id/searchView")
        btnSearchView.click()
        time.sleep(1)

        #Ввод фамилии Иванов (поле SearchSrcText)
        editSearchSrcText = self.driver.find_element_by_id("android:id/search_src_text")
        editSearchSrcText.send_keys("Васильев")
        time.sleep(1)

        #После поиска выбираем конкретного пользователя
        self.driver.find_element_by_id("net.gas.gascontact:id/personList")
        elements = self.driver.find_elements_by_id("net.gas.gascontact:id/text_name")

        for element in elements:
            if element.text == "Васильев Владислав Юрьевич":
                element.click()
                time.sleep(2)
                break

        #Проверка на заполнение поле мобильного телефона
        self.driver.find_element_by_id("net.gas.gascontact:id/mobileNumberFrame")
        textMobile = self.driver.find_element_by_id("net.gas.gascontact:id/text_mobile")
        if textMobile.text == "":
            print ("Error 1: Отсуствует номер")
        else:
            time.sleep(10)

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()

if __name__ == "__main__":
    SUITE = unittest.TestLoader().loadTestsFromTestCase(ContackBookAppTestUpdateDBAppium)
    unittest.TextTestRunner(verbosity=2).run(SUITE)
