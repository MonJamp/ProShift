from django.test import TestCase
from django.urls import reverse
from rest_framework.test import APITestCase
from rest_framework.authtoken.models import Token
from django.contrib.auth.models import User
from rest_framework import status

class AccountsTest(APITestCase):
    def setUp(self):
        # We want to go ahead and originally create a user. 
        self.test_user = User.objects.create_user('testuser', 'test@example.com', 'testpassword')
        self.test_user.first_name = 'first'
        self.test_user.last_name = 'last'

        # URL for creating an account.
        self.create_url = reverse('api_register')

    def test_create_user(self):
        """
        Ensure we can create a new user and a valid token is created with it.
        """
        data = {
            'username': 'foobar',
            'email': 'foobar@example.com',
            'first_name': 'foo',
            'last_name': 'bar',
            'password': 'somepassword'
        }

        response = self.client.post(self.create_url , data, format='json')
        user = User.objects.latest('id')
        token = Token.objects.get(user=user)

        # We want to make sure we have two users in the database..
        self.assertEqual(User.objects.count(), 2)
        # And that we're returning a 201 created code.
        self.assertEqual(response.status_code, status.HTTP_201_CREATED)
        # Additionally, we want to return the username and email upon successful creation.
        self.assertEqual(response.data['username'], data['username'])
        self.assertEqual(response.data['email'], data['email'])
        self.assertFalse('password' in response.data)
        self.assertEqual(response.data['token'], token.key)
    
    def test_create_user_with_short_password(self):
        """
        Ensure user is not created for password lengths less than 8.
        """
        data = {
                'username': 'foobar',
                'email': 'foobarbaz@example.com',
                'first_name': 'foo',
                'last_name': 'bar',
                'password': 'foo'
        }

        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)
        self.assertEqual(len(response.data['password']), 1)

    def test_create_user_with_no_password(self):
        data = {
                'username': 'foobar',
                'email': 'foobarbaz@example.com',
                'first_name': 'foo',
                'last_name': 'bar',
                'password': ''
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
                'password': 'somepassword'
        }
        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)

    def test_create_user_with_no_email(self):
        data = {
                'username': 'foobar',
                'email': '',
                'first_name': 'foo',
                'last_name': 'bar',
                'password': 'somepassword'
        }
        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)
    
    def test_create_user_with_no_firstname(self):
        data = {
                'username': 'foobar',
                'email': 'foobarbaz@example.com',
                'first_name': '',
                'last_name': 'bar',
                'password': 'somepassword'
        }
        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)

    def test_create_user_with_no_lastname(self):
        data = {
                'username': 'foobar',
                'email': 'foobarbaz@example.com',
                'first_name': 'foo',
                'last_name': '',
                'password': 'somepassword'
        }
        response = self.client.post(self.create_url, data, format='json')
        self.assertEqual(response.status_code, status.HTTP_400_BAD_REQUEST)
        self.assertEqual(User.objects.count(), 1)  

    #def test_create_user_that_already_exists(self):
        # we want to make sure that no 2 users have the same username
    
    #def test_create_user_with_nonunique_password(self):
        # we want to make sure that each user has a unique password
    
    #def test_create_user_nonunique_usernames_and_emails(self):
        # want to make sure that the username has entered a unique username and email
