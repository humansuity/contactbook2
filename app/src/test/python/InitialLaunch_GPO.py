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

class MriradaAppTest1Appium(unittest.TestCase):
    """ Класс тестирования мобильной Мириады Тест1"""

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
        }
        cls.driver = webdriver.Remote("http://localhost:4723/wd/hub", desired_caps)

    def test_enter(self):
        """ Тестирвоание первого входа (авторизация)"""
        time.sleep(4)
        # Поиск элемента под названием "Загрузки" и нажимаем кнопку
        self.driver.find_element_by_id("net.gas.gascontact:id/main_toolbar")
        self.driver.find_element_by_id("net.gas.gascontact:id/alertContainer")
        elements = self.driver.find_elements_by_id("net.gas.gascontact:id/alertContainer")
        btnEnter = self.driver.find_element_by_id("net.gas.gascontact:id/button")
        btnEnter.click()
        time.sleep(1)

        # Вылетает окно с Allow. Нажимаем на кнопку Allow
        self.driver.find_element_by_id("com.android.packageinstaller:id/dialog_container")
        self.driver.find_element_by_id("com.android.packageinstaller:id/desc_container")
        time.sleep(1)
        btnAllow=self.driver.find_element_by_id("com.android.packageinstaller:id/permission_allow_button")
        btnAllow.click()
        time.sleep(1)

        #Нажать на кнопку выпадающего списка с облостями
        self.driver.find_element_by_id("net.gas.gascontact:id/fragmentHolder")
        btnSpiner = self.driver.find_element_by_id("net.gas.gascontact:id/spinner")
        btnSpiner.click()
        time.sleep(2)

        #Работа со списком
        self.driver.find_element_by_id("net.gas.gascontact:id/contentPanel")
        self.driver.find_element_by_id("net.gas.gascontact:id/select_dialog_listview")

        #Скролл списка
        self.driver.swipe(70, 510, 70, 100, 400)

        elementsList = self.driver.find_elements_by_id("net.gas.gascontact:id/realmDescription")

        #Для БелТопГаз
        for element in elementsList:
            if element.text == "ГПО Белтопгаз":
                element.click()
                time.sleep(2)
                break


        # Ввод данных - Логин и Пароль
        loginUser = self.driver.find_element_by_id("net.gas.gascontact:id/loginUsernameText")
        loginUser.send_keys("angurla")
        time.sleep(1)
        passwordUser = self.driver.find_element_by_id("net.gas.gascontact:id/loginPasswordText")
        passwordUser.send_keys("12358")
        time.sleep(1)
        # Нажать кнопку ВОЙТИ
        buttonEnter = self.driver.find_element_by_id("net.gas.gascontact:id/loginButton")
        buttonEnter.click()
        time.sleep(2)

        #Проверка загрузка БД
        self.driver.find_element_by_id("net.gas.gascontact:id/textView2")
        editStatus = self.driver.find_element_by_id("net.gas.gascontact:id/textView5")

        if (editStatus.text == "База данных загружается…"):
            time.sleep(10)
        else:
            print ("Error 1: No load form LOAD")

        time.sleep(10)

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()

if __name__ == "__main__":
    SUITE = unittest.TestLoader().loadTestsFromTestCase(MriradaAppTest1Appium)
    unittest.TextTestRunner(verbosity=2).run(SUITE)
