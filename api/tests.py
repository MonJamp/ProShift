# Copyright 2020 ProShift Team
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.

from rest_framework.test import APITestCase
from rest_framework import status
from django.urls import reverse
from django.contrib.auth import get_user_model

User = get_user_model()

class AccountsTest(APITestCase):
    def setUp(self):
        # We want to go ahead and originally create a user. 
        self.test_user = User.objects.create(username='testuser', email='test@example.com', password='testpassword')
        self.test_user.first_name = 'first'
        self.test_user.last_name = 'last'

        self.assertTrue(isinstance(self.test_user, User))
        
        # URL for creating an account.
        self.create_url = reverse('user-list')

    def test_create_user(self):
        """
        Ensure we can create a new user and a valid token is created with it.
        """
        data = {
            'username': 'foobar',
            'email': 'foobar@example.com',
            'first_name': 'foo',
            'last_name': 'bar',
            'phone': '',
            'company_code': '',
            'password': 'somepassword',
            're_password': 'somepassword'
        }

        response = self.client.post(self.create_url , data, format='json')
        user = User.objects.latest('id')


        # And that we're returning a 201 created code.
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        # We want to make sure we have two users in the database..
        self.assertEqual(User.objects.count(), 2)
        # Additionally, we want to return the username and email upon successful creation.
        self.assertEqual(response.data['username'], data['username'])
        self.assertEqual(response.data['email'], data['email'])
        self.assertFalse('password' in response.data)
        self.assertTrue(user.is_authenticated)
    
    def test_create_user_with_short_password(self):
        """
        Ensure user is not created for password lengths less than 8.
        """
        data = {
                'username': 'foobar',
                'email': 'foobarbaz@example.com',
                'first_name': 'foo',
                'last_name': 'bar',
                'phone': '',
                'company_code': '',
                'password': 'short',
                're_password': 'short'
        }

        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)
        # Server will response by saying password is too short
        self.assertEqual(len(response.data['password']), 1)

    def test_create_user_with_no_password(self):
        data = {
                'username': 'foobar',
                'email': 'foobarbaz@example.com',
                'first_name': 'foo',
                'last_name': 'bar',
                'phone': '',
                'company_code': '',
                'password': '',
                're_password': 'short'
        }

        response = self.client.post(self.create_url, data, format='json')
        # make sure that the request didn't go through(bad request status response)
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        # make sure the user account wasn't added to the database
        self.assertEqual(User.objects.count(), 1)
        self.assertEqual(len(response.data['password']), 1)

    def test_create_user_with_no_username(self):
        data = {
                'username': '',
                'email': 'foobarbaz@example.com',
                'first_name': 'foo',
                'last_name': 'bar',
                'phone': '',
                'company_code': '',
                'password': 'somepassword',
                're_password': 'somepassword'
        }

        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)
        self.assertEqual(len(response.data['username']), 1)

    def test_create_user_with_no_email(self):
        data = {
                'username': 'foobar',
                'email': '',
                'first_name': 'foo',
                'last_name': 'bar',
                'phone': '',
                'company_code': '',
                'password': 'somepassword',
                're_password': 'somepassword'
        }

        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)
        self.assertEqual(len(response.data['email']), 1)
    
    def test_create_user_with_no_firstname(self):
        data = {
                'username': 'foobar',
                'email': 'foobarbaz@example.com',
                'first_name': '',
                'last_name': 'bar',
                'phone': '',
                'company_code': '',
                'password': 'somepassword',
                're_password': 'somepassword'
        }

        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)
        self.assertEqual(len(response.data['first_name']), 1)

    def test_create_user_with_no_lastname(self):
        data = {
                'username': 'foobar',
                'email': 'foobarbaz@example.com',
                'first_name': 'foo',
                'last_name': '',
                'phone': '',
                'company_code': '',
                'password': 'somepassword',
                're_password': 'somepassword'
        }

        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)
        self.assertEqual(len(response.data['last_name']), 1)

    #def test_create_user_that_already_exists(self):
        # we want to make sure that no 2 users have the same username
    
    #def test_create_user_with_nonunique_password(self):
        # we want to make sure that each user has a unique password
    
    #def test_create_user_nonunique_usernames_and_emails(self):
        # want to make sure that the username has entered a unique username and email
