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
        # нажание на кнопку ADD
        self.driver.find_element_by_id("net.gas.gascontact:id/root")
        btnAdd = self.driver.find_element_by_class_name("android.widget.ImageView")
        btnAdd.click()
        time.sleep(2)

        #Выбрать Обновление БД
        self.driver.find_element_by_id("net.gas.gascontact:id/content")
        elementListAdd = self.driver.find_elements_by_id("net.gas.gascontact:id/title")

        for element in elementListAdd:
            if element.text == "Обновление БД":
                element.click()
                time.sleep(2)
                break

        time.sleep(2)

        #Нажатие на кнопку "Обновить"
        self.driver.find_element_by_id("android:id/content")
        self.driver.find_element_by_id("android:id/custom")
        btnUpdate = self.driver.find_element_by_id("net.gas.gascontact:id/btn_ok_update")
        btnUpdate.click()
        time.sleep(2)

        #Ввод Логина и Пароля
        loginUser = self.driver.find_element_by_id("net.gas.gascontact:id/loginUsernameText")
        loginUser.send_keys("angurla")
        time.sleep(1)
        passwordUser = self.driver.find_element_by_id("net.gas.gascontact:id/loginPasswordText")
        passwordUser.send_keys("12358")
        time.sleep(1)
        # Нажать кнопку ВОЙТИ
        buttonEnter = self.driver.find_element_by_id("net.gas.gascontact:id/loginButton")
        buttonEnter.click()

        time.sleep(10)

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()

if __name__ == "__main__":
    SUITE = unittest.TestLoader().loadTestsFromTestCase(ContackBookAppTestUpdateDBAppium)
    unittest.TextTestRunner(verbosity=2).run(SUITE)
