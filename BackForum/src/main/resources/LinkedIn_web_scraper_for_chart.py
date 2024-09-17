import csv
import requests
from bs4 import BeautifulSoup
import unicodedata
import re
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
import os

def normalize_string(string):
    return unicodedata.normalize('NFKD', string).encode('ascii', 'ignore').decode('utf-8')

def determine_speciality(title):
    title_lower = title.lower()
    title_lower = re.sub(r'[^\w\s]', '', title_lower)
    keywords = {
        # Developpement web et mobile
        'DWM': ['Software','Developpeur','react native', 'ios', 'swift', 'kotlin', 'mobile', 'android', 'flutter','java', 'web', 'fullstack', 'full stack', 'node.js','node js', 'express js', '.net', 'angular', 'dotnet', 'vue js', 'react', 'html', 'css', 'javascript', 'springboot', 'frontend', 'front-end', 'backend', 'back-end', 'php', 'symfony', 'laravel', 'mobiles','mobile','wordpress'],
        # Dig data et bi
        'BI&BD': ['Analytics','power bi', 'ssis', 'ssrs', 'ssas', 'business intelligence', 'big data', 'sql', 'administrateur', 'oracle','python', 'machine learning', 'science', 'r', 'scientist','talend','excel'],
        # Embarque
        'ES': ['embedded','cad', 'c++', 'c', 'autocad'],
        # Cloud
        'CL': ['linux', 'devops', 'ip', 'cloud', 'terraform', 'grafana', 'apache airflow', 'sAcuritA','security'],
    }
    
    max_similarity = 0
    best_speciality = 'AUTRE'
    all_keywords = [' '.join(word_list) for word_list in keywords.values()]
    all_keywords.append(title_lower)
    vectorizer = TfidfVectorizer()
    vectors = vectorizer.fit_transform(all_keywords)
   
    title_vector = vectors[-1]  
    for i, speciality_vector in enumerate(vectors[:-1]):  
        similarity = np.dot(speciality_vector, title_vector.T).toarray()[0][0]
        if similarity > max_similarity:
            max_similarity = similarity
            best_speciality = list(keywords.keys())[i]    
    return best_speciality

def linkedin_scraper(webpage, page_number, writer):
    next_page = webpage + str(page_number)
    print(next_page)
    response = requests.get(next_page)
    soup = BeautifulSoup(response.content,'html.parser')

    jobs = soup.find_all('div', class_='base-card relative w-full hover:no-underline focus:no-underline base-card--link base-search-card base-search-card--link job-search-card')
    for job in jobs:
        try:
            job_title = normalize_string(job.find('h3', class_='base-search-card__title').text.strip())
        except AttributeError:
            job_title = 'Not available'
        try:
            job_company = normalize_string(job.find('h4', class_='base-search-card__subtitle').text.strip())
        except AttributeError:
            job_company = 'Not available'
        try:
            job_location = normalize_string(job.find('span', class_='job-search-card__location').text.strip())
        except AttributeError:
            job_location = 'Not available'
        try:
            job_link = job.find('a', class_='base-card__full-link')['href']
        except (AttributeError, KeyError):
            job_link = 'Not available'

        writer.writerow([
            job_title,
            job_company,
            job_location,
            job_link,
        ])

    print('Data updated')

    if page_number < 500:
        page_number = page_number + 10
        linkedin_scraper(webpage, page_number, writer)
    else:
        print('Scraping completed')
        input_file_path = 'linkedin-jobs.csv'
        output_file_path = r'C:\Users\MSII\Desktop\PI\ACTUAL WORK\Front\FontPiDev\src\assets\linkedin-jobs-noduplicates.csv'
        remove_duplicates(input_file_path, output_file_path)

def remove_duplicates(input_file, output_file):
    with open(input_file, 'r', newline='', encoding='utf-8') as infile, \
         open(output_file, 'w', newline='', encoding='utf-8') as outfile:
        reader = csv.reader(infile)
        writer = csv.writer(outfile)
        seen = set()
        title_index = None
        for i, row in enumerate(reader):
            if i == 0:  
                title_index = row.index('Title')
                writer.writerow(['Title', 'Speciality']) 
                continue
            row_key = row[title_index]
            if row_key not in seen:
                speciality = determine_speciality(row[title_index])
                writer.writerow([row[title_index], speciality])  
                seen.add(row_key)

def main():
    with open('linkedin-jobs.csv', 'a', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(['Title', 'Company', 'Location', 'Apply'])  
        linkedin_scraper('https://www.linkedin.com/jobs-guest/jobs/api/seeMoreJobPostings/search?keywords=Software%2BEngineer&location=Tunisia&geoId=102134353&trk=public_jobs_jobs-search-bar_search-submit&start=', 0, writer)

if __name__ == "__main__":
    main()
