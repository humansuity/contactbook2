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

__author__ = "Uladzislau Vasilyeu"

class MriradaAppTest1Appium(unittest.TestCase):
    """ Класс тестирования мобильной Мириады Тест1"""

# UiAutomator2
    @classmethod
    def setUpClass(cls):
        # Делаем необходимые настройки, данные приложения обнуляются
        desired_caps = {
                'platformName': 'android',
                'deviceName': 'emulator-5554',
                'appActivity': 'net.gas.miriada.ui.splash.SplashActivity',
                'appPackage': 'net.gas.miriada',
                'automationName': 'UiAutomator2',
                }
        cls.driver = webdriver.Remote("http://localhost:4723/wd/hub", desired_caps)

    def test_first_login_window(self):
        """ Тестирование первого запуска программы c входом в систему """
        # Выбор компании и ввод логина и пароля
        time.sleep(4)
        self.driver.find_element_by_id("net.gas.miriada:id/toolbar_org")
        self.driver.find_element_by_id("net.gas.miriada:id/orglistView")
        elements = self.driver.find_elements_by_id("net.gas.miriada:id/title")
        # Ищем в списке элемент с "Витебскоблгаз"
        for element in elements:
            if element.text == "Витебскоблгаз":
                element.click()
                time.sleep(2)
                break
        el2 = self.driver.find_element_by_id("net.gas.miriada:id/loginUsernameText")
        el2.send_keys("kozlovmv")
        time.sleep(2)
        el3 = self.driver.find_element_by_id("net.gas.miriada:id/loginPasswordText")
        el3.send_keys("12358")
        time.sleep(2)
        el4 = self.driver.find_element_by_id("net.gas.miriada:id/loginButton")
        el4.click()
        time.sleep(2)
        # Проверяем на основное окно
        self.driver.find_element_by_id("net.gas.miriada:id/my_nav_host_organizations_list_fragment")
        time.sleep(2)

    @classmethod
    def tearDownClass(cls):
        cls.driver.quit()

if __name__ == "__main__":
    SUITE = unittest.TestLoader().loadTestsFromTestCase(MriradaAppTest1Appium)
    unittest.TextTestRunner(verbosity=2).run(SUITE)
