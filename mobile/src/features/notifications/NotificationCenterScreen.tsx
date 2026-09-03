import React from 'react';
import { View, Text, StyleSheet, FlatList } from 'react-native';

const mockNotifications = [
  { id: '1', title: 'Application Submitted', message: 'Your SSC CGL application has been submitted successfully.', time: '2h ago', type: 'SUCCESS' },
  { id: '2', title: 'Deadline Reminder', message: 'IBPS PO deadline is tomorrow. Apply now!', time: '5h ago', type: 'WARNING' },
  { id: '3', title: 'Payment Success', message: 'Payment for Application #123 was successful.', time: '1d ago', type: 'INFO' },
];

const NotificationCenterScreen = () => {
  return (
    <View style={styles.container}>
      <FlatList
        data={mockNotifications}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <View style={styles.card}>
            <View style={styles.header}>
              <Text style={styles.title}>{item.title}</Text>
              <Text style={styles.time}>{item.time}</Text>
            </View>
            <Text style={styles.message}>{item.message}</Text>
          </View>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5', padding: 10 },
  card: { backgroundColor: '#fff', padding: 15, borderRadius: 8, marginBottom: 10, borderLeftWidth: 4, borderLeftColor: '#2196f3' },
  header: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 5 },
  title: { fontWeight: 'bold', fontSize: 15 },
  time: { fontSize: 11, color: '#999' },
  message: { fontSize: 13, color: '#666', lineHeight: 18 }
});

export default NotificationCenterScreen;
