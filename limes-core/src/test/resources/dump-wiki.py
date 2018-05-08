import re
from collections import defaultdict

vital_articles = """
The_arts
Architecture
Film
Literature
Music
Performing_arts
Visual_arts
Earth
Africa
Asia
City
Country
Europe
Geography
Land
North_America
Oceania
Sea
South_America
History_of_the_world
Ancient_history
History
History_of_art
History_of_science
Modern_history
Post
Prehistory
Book
Clothing
Communication
Death
Emotion
Entertainment
Food
Home
Human_sexuality
Sleep
Time
Writing
Mathematics
Arithmetic
Geometry
Number
Statistics
Ethics
Knowledge
Logic
Mind
Mythology
Philosophy
Religion
Human
Life
Science
Animal
Astronomy
Atom
Biology
Cell_
Chemical_element
Chemistry
Climate
Disease
Electricity
Energy
Evolution
Medicine
Nature
Physics
Plant
Psychology
Universe
Water
Culture
Language
Business
Civilization
Community
Crime
Education
Employment
Ethnic_group
Family
Government
Law
Money
Politics
Popular_culture
Society
War
Technology
Agriculture
Computer
Construction
Engineering
Fire
Industry
Manufacturing
Tool
Transport
""".strip().split("\n")

basic_query = "https://{}.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exlimit=max&explaintext&exintro&titles={}&redirects="

from urllib.request import urlopen

def extract(lang, article_list):
	query = basic_query.format(lang, "|".join(article_list))
	text = str(urlopen(query).read())
	text = text.replace("\\\\n", " ")
	text = text.replace("\\\\\"", "\"")
	text = text.replace("\\'", "'")
	text = text.replace("\\\\u2013", " - ")
	text = text.replace("\\\\u2014", " - ")
	good_lines = [it for it in text.split("},") if "title" in it and "extract" in it]
	res = dict()
	for line in good_lines:
		print(line)
		title = re.findall('"title":"([^"]+)"', line)[0]
		abstract = re.findall('"extract":"(.+)"', line)[0]
		print(title, abstract)
		res[title] = abstract
	return res

if __name__ == "__main__":
	dic = defaultdict(list)
	for i in range(0, len(vital_articles), 20):
		simple = extract("simple", vital_articles[i:i+20])
		en = extract("en", vital_articles[i:i + 20])
		for a,b in simple.items():
			dic[a].append(b)
		for a,b in en.items():
			dic[a].append(b)
	print("="*20)
	for a,b in dic.items():
		if len(b) == 2:
			print("{}\t{}\t{}".format(a,b[0],b[1]))