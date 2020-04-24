from os import path

import requests
import time
import random
import os
import json
import traceback
from requests.exceptions import HTTPError
from bs4 import BeautifulSoup

#clear console
def clear():
    if os.name == 'nt':
        os.system('cls')
    else:
        os.system('clear')

#Stores recipe info
class Recipe:
    def __init__(self):
        title = ""
        image = ""
        imageFileName = ""
        category = ""
        ingredients = []
        steps = []

    def __str__(self):
        if not hasattr(self, 'title'):
            print("Error: Missing title")
            return ""
        string = self.title + "\n" + self.image + "\n" + self.category + "\n"
        for i in self.ingredients:
            string += i + "\n"
        for i in self.steps:
            string += i + "\n"
        return string

    def __lt__(self, other):
        return self.title < other.title

    def __gt__(self, other):
        return self.title > other.title

    def ingredientsToDict(self):
        ing = {}
        count = 1
        for i in self.ingredients:
            ing['i' + str(count).zfill(4)] = i
            count += 1
        return ing

    def stepsToDict(self):
        steps = {}
        count = 1
        for i in self.steps:
            steps['s' + str(count).zfill(4)] = i
            count += 1
        return steps

    def recipeToDict(self):
        if len(self.imageFileName) < 1:
            self.imageFileName = "Default.jpg"
        dict = {'title': self.title, 'image': self.image, 'imageFileName': self.imageFileName, 'ingredients': self.ingredientsToDict(), 'steps': self.stepsToDict()}
        return dict

    def setUpImage(self, num):
        try:
            file = open("resources\\" + "image" + str(num) + ".jpg", 'wb')
            file.write(requests.get(self.image).content)
            self.imageFileName = "image" + str(num) + ".jpg"
            return True
        except Exception as error:
            self.imageFileName = "Default.jpg"
            print(traceback.format_exc())
            print("Image request error")
            return False

    def check(self):
        return hasattr(self, 'title') and hasattr(self, 'category') and hasattr(self, 'image')

class Scraper:
    unknownCategoriesCount = 0
    imageCount = 0

    #scrape recipe info from allrecipes.com url, returns recipe object
    def scrapeURL(self, url, category):
        recipe = Recipe()
        try:
            page = requests.get(url)
            page.raise_for_status()
            parser = BeautifulSoup(page.text, 'html.parser')
            titleSpan = parser.find('h1')
            title = titleSpan.contents[0]

            imageClass = parser.find(class_="rec-photo")
            image = imageClass['src']

            ingredients = parser.findAll(class_="recipe-ingred_txt added")
            ingredientsList = []
            for i in ingredients:
                ingredientsList.append(i.contents[0])

            steps = parser.findAll(class_="recipe-directions__list--item")
            stepsList = []
            if len(steps) < 1:
                raise Exception('No recipe steps')
            for i in steps:
                for j in i:
                    stepsList.append(j.rstrip())
            setattr(recipe, 'title', title)
            setattr(recipe, 'image', image)
            setattr(recipe, 'category', category)
            setattr(recipe, 'ingredients', ingredientsList)
            setattr(recipe, 'steps', stepsList)
        except HTTPError as httpError:
            print("HTTP Error :", httpError)
        except Exception as error:
            print("Other error: ", error)
            print(traceback.format_exc())
            print("Error with url: " + url)
        return recipe

    #scrapes a list of recipe urls from a category url
    def scrapeURLS(self, url, max):
        urls = []
        try:
            page = requests.get(url)
            page.raise_for_status()
            parser = BeautifulSoup(page.text, 'html.parser')
            recipeClass = parser.find_all(class_="fixed-recipe-card__title-link", href=True)
            count = 1
            urlsAvailable = len(recipeClass)
            if max < urlsAvailable:
                urlsAvailable = max
            for i in recipeClass[0:urlsAvailable]:
                urls.append(i['href'])
                time.sleep(random.random()*2)
                print("URL scraper, getting url " + str(count) + " of " + str(urlsAvailable))
                count += 1
        except HTTPError as httpError:
            print("HTTP Error :", httpError)
        except Exception as error:
            print("Other error: ", error)
            print(traceback.format_exc())
        return urls

    #scrapes the name of a category from a category url
    def scrapeCategoryName(self, url):
        try:
            page = requests.get(url)
            page.raise_for_status()
            parser = BeautifulSoup(page.text, 'html.parser')
            categorySpan = parser.find(class_="title-section__text title")
            return categorySpan.contents[0]
        except HTTPError as httpError:
            self.unknownCategoriesCount += 1
            print("HTTP Error :", httpError)
            return "Unknown Category: " + str(self.unknownCategoriesCount)
        except Exception as error:
            self.unknownCategoriesCount += 1
            print("Other error: ", error)
            print(traceback.format_exc())
            return "Unknown Category: " + str(self.unknownCategoriesCount)

    #Scrapes recipes from a category, returns a list of recipe objects with info from it
    def scrapeCategories(self, url, number):
        recipes = []
        try:
            category = self.scrapeCategoryName(url)
            recipes = []
            recipeUrls = []
            count = 1
            page = 0
            while count < number+1:
                print("Category scraper getting recipe " + str(count) + " of " + str(number))
                while len(recipeUrls) < 1:
                    recipeUrls.extend(self.scrapeURLS(url + "?page=" + str(page), number - count + 1))
                    page += 2
                recipe = self.scrapeURL(recipeUrls.pop(0), category)
                if recipe.check() and recipe.setUpImage(self.imageCount):
                    recipes.append(recipe)
                    count += 1
                    self.imageCount += 1
                    time.sleep(random.random() * 2)
                else:
                    print("scrapeURL error")
        except HTTPError as httpError:
            print("HTTP Error :", httpError)
        except Exception as error:
            print("Other error: ", error)
            print(traceback.format_exc())
        return recipes

def main():
    urls = []
    urls.append("https://www.allrecipes.com/recipes/78/breakfast-and-brunch/")
    urls.append("https://www.allrecipes.com/recipes/17561/lunch/")
    urls.append("https://www.allrecipes.com/recipes/17562/dinner/")
    urls.append("https://www.allrecipes.com/recipes/76/appetizers-and-snacks/")
    urls.append("https://www.allrecipes.com/recipes/79/desserts/")
    urls.append("https://www.allrecipes.com/recipes/77/drinks/")
    urlCounts = [50, 100, 100, 50, 50, 25]
    s = Scraper()
    recipes = []
    for u in urls:
        temp = s.scrapeCategories(u,urlCounts.pop(0))
        temp.sort()
        recipes.extend(temp)
    data = {}
    count = 0
    for i in recipes:
        if not i.category in data:
            data[i.category] = {}
        data[i.category]["r" + str(count).zfill(4)] = i.recipeToDict()
        count += 1
    with open('recipes.json', 'w') as output:
        json.dump(data, output)
    print(json.dumps(data, indent=4))

main()
