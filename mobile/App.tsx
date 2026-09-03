import React from 'react';
import { NavigationContainer } from '@react-navigation/native';
import { createStackNavigator } from '@react-navigation/stack';
import { Provider } from 'react-redux';
import { store } from './src/store/store';
import LoginScreen from './src/features/auth/LoginScreen';
import ProfileScreen from './src/features/profile/ProfileScreen';
import DocumentVaultScreen from './src/features/documents/DocumentVaultScreen';
import JobHomeScreen from './src/features/jobs/JobHomeScreen';
import JobDetailScreen from './src/features/jobs/JobDetailScreen';
import ApplicationReadinessScreen from './src/features/applications/ApplicationReadinessScreen';
import MyApplicationsScreen from './src/features/applications/MyApplicationsScreen';
import ApplicationDetailScreen from './src/features/applications/ApplicationDetailScreen';
import CheckoutScreen from './src/features/payments/CheckoutScreen';
import SupportHomeScreen from './src/features/support/SupportHomeScreen';
import NotificationCenterScreen from './src/features/notifications/NotificationCenterScreen';
import SettingsScreen from './src/features/settings/SettingsScreen';

const Stack = createStackNavigator();

const App = () => {
  return (
    <Provider store={store}>
      <NavigationContainer>
        <Stack.Navigator initialRouteName="Login">
          <Stack.Screen name="Login" component={LoginScreen} />
          <Stack.Screen name="Profile" component={ProfileScreen} />
          <Stack.Screen name="DocumentVault" component={DocumentVaultScreen} />
          <Stack.Screen name="JobHome" component={JobHomeScreen} options={{ title: 'Find Jobs' }} />
          <Stack.Screen name="JobDetail" component={JobDetailScreen} options={{ title: 'Job Details' }} />
          <Stack.Screen name="ApplicationReadiness" component={ApplicationReadinessScreen} options={{ title: 'Check Readiness' }} />
          <Stack.Screen name="MyApplications" component={MyApplicationsScreen} options={{ title: 'My Applications' }} />
          <Stack.Screen name="ApplicationDetail" component={ApplicationDetailScreen} options={{ title: 'Tracking' }} />
          <Stack.Screen name="Checkout" component={CheckoutScreen} options={{ title: 'Payment' }} />
          <Stack.Screen name="SupportHome" component={SupportHomeScreen} options={{ title: 'Help & Support' }} />
          <Stack.Screen name="Notifications" component={NotificationCenterScreen} options={{ title: 'Notifications' }} />
          <Stack.Screen name="Settings" component={SettingsScreen} options={{ title: 'Settings' }} />
        </Stack.Navigator>
      </NavigationContainer>
    </Provider>
  );
};

export default App;
