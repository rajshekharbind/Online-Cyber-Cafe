import React from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';

const mockApplications = [
  { id: '1', jobTitle: 'SSC CGL 2024', status: 'SUBMITTED', date: '2024-07-20', appNo: 'APP-2024-001' },
  { id: '2', jobTitle: 'IBPS PO XIV', status: 'IN_PROGRESS', date: '2024-08-05', appNo: 'PENDING' },
];

const MyApplicationsScreen = ({ navigation }: any) => {
  return (
    <View style={styles.container}>
      <FlatList
        data={mockApplications}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={styles.card}
            onPress={() => navigation.navigate('ApplicationDetail', { id: item.id })}
          >
            <View style={styles.header}>
              <Text style={styles.title}>{item.jobTitle}</Text>
              <View style={[styles.badge, { backgroundColor: getStatusColor(item.status) }]}>
                <Text style={styles.badgeText}>{item.status}</Text>
              </View>
            </View>
            <Text style={styles.date}>Applied on: {item.date}</Text>
            <Text style={styles.appNo}>App No: {item.appNo}</Text>
          </TouchableOpacity>
        )}
      />
    </View>
  );
};

const getStatusColor = (status: string) => {
  switch (status) {
    case 'SUBMITTED': return '#4caf50';
    case 'IN_PROGRESS': return '#2196f3';
    case 'PAYMENT_PENDING': return '#ff9800';
    case 'FAILED': return '#f44336';
    default: return '#757575';
  }
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5', padding: 10 },
  card: { backgroundColor: '#fff', padding: 15, borderRadius: 10, marginBottom: 12, elevation: 2 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  title: { fontSize: 18, fontWeight: 'bold', color: '#333' },
  badge: { paddingHorizontal: 8, paddingVertical: 4, borderRadius: 4 },
  badgeText: { fontSize: 10, color: '#fff', fontWeight: 'bold' },
  date: { color: '#666', marginTop: 8, fontSize: 12 },
  appNo: { color: '#333', marginTop: 4, fontWeight: '500' }
});

export default MyApplicationsScreen;
