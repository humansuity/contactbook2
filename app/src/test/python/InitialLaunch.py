#!/usr/bin/python3
# -*- coding: utf-8 -*-
# This sample code uses the Appium python client
# pip install Appium-Python-Client
# Then you can paste this into a file and simply run with Python
""" Test for Miriada Copyright 2020 """

import unittest
import time
from appium import webdriver
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
                'appActivity': 'net.gas.gascontact.ui.activities.SplashActivity',
                'appPackage': 'net.gas.gascontact',
                'automationName': 'UiAutomator2',
                }
        cls.driver = webdriver.Remote("http://localhost:4723/wd/hub", desired_caps)

    def test_enter(self):
        """ Тестирвоание первого входа (авторизация)"""
        time.sleep(4)
        self.driver.find_element_by_id("net.gas.gascontact:id/main_toolbar")
        self.driver.find_element_by_id("net.gas.gascontact:id/alertContainer")
        elements = self.driver.find_elements_by_id("net.gas.gascontact:id/alertContainer")
        # Поиск элемента под названием "Загрузки"
        btnEnter = self.driver.find_element_by_id("net.gas.gascontact:id/button")
        btnEnter.click()
        time.sleep(1)
        self.driver.find_element_by_id("com.android.packageinstaller:id/dialog_container")
        self.driver.find_element_by_id("com.android.packageinstaller:id/desc_container")

        time.sleep(1)
        print("btnAllow")
        btnAllow=self.driver.find_element_by_id("com.android.packageinstaller:id/permission_allow_button")
        print("Click Allow")
        btnAllow.click()
        time.sleep(1)
        self.driver.find_element_by_id("net.gas.gascontact:id/fragmentHolder")
        btnSpiner = self.driver.find_element_by_id("net.gas.gascontact:id/spinner")
        print("Click List")
        btnSpiner.click()
        time.sleep(2)


        #Поиск списка с облостями
        self.driver.find_element_by_id("net.gas.gascontact:id/contentPanel")
        self.driver.find_element_by_id("net.gas.gascontact:id/select_dialog_listview")
        elementsList = self.driver.find_elements_by_id("net.gas.gascontact:id/realmDescription")

        #Для ВитебскОблГаза
        for element in elementsList:
            if element.text == "Витебскоблгаз":
                print("Click Витебскоблгаз")
                element.click()
                time.sleep(2)
                break


        # Ввод данных - Логин и Пароль
        loginUser = self.driver.find_element_by_id("net.gas.gascontact:id/loginUsernameText")
        loginUser.send_keys("kozlovmv")
        time.sleep(1)
        passwordUser = self.driver.find_element_by_id("net.gas.gascontact:id/loginPasswordText")
        passwordUser.send_keys("12358")
        time.sleep(1)
        print("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        # Нажать кнопку ВОЙТИ
        buttonEnter = self.driver.find_element_by_id("net.gas.gascontact:id/loginButton")
        print("Click Enter")
        buttonEnter.click()
        time.sleep(2)
        # Проверяем на основное окно
        #self.driver.find_element_by_id("net.gas.miriada:id/my_nav_host_organizations_list_fragment")
        #time.sleep(2)

#    def test_first_login_window(self):
#        """ Тестирование первого запуска программы c входом в систему """
#        # Выбор компании и ввод логина и пароля
#        time.sleep(4)
#        self.driver.find_element_by_id("net.gas.miriada:id/toolbar_org")
#        self.driver.find_element_by_id("net.gas.miriada:id/orglistView")
#        elements = self.driver.find_elements_by_id("net.gas.miriada:id/title")
#        # Ищем в списке элемент с "Витебскоблгаз"
#        for element in elements:
#            if element.text == "Витебскоблгаз":
#                element.click()
#                time.sleep(2)
#                break
#        el2 = self.driver.find_element_by_id("net.gas.miriada:id/loginUsernameText")
#        el2.send_keys("kozlovmv")
#        time.sleep(2)
#        el3 = self.driver.find_element_by_id("net.gas.miriada:id/loginPasswordText")
#        el3.send_keys("12358")
#        time.sleep(2)
#        el4 = self.driver.find_element_by_id("net.gas.miriada:id/loginButton")
#        el4.click()
#        time.sleep(2)
#        # Проверяем на основное окно
#        self.driver.find_element_by_id("net.gas.miriada:id/my_nav_host_organizations_list_fragment")
#        time.sleep(2)
#
    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()

if __name__ == "__main__":
    SUITE = unittest.TestLoader().loadTestsFromTestCase(MriradaAppTest1Appium)
    unittest.TextTestRunner(verbosity=2).run(SUITE)
