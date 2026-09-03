import React from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity } from 'react-native';

const mockTickets = [
  { id: '1', subject: 'Payment failed but amount deducted', status: 'RESOLVED', date: '2024-07-20' },
  { id: '2', subject: 'Document upload error', status: 'OPEN', date: '2024-08-10' },
];

const SupportHomeScreen = ({ navigation }: any) => {
  return (
    <View style={styles.container}>
      <TouchableOpacity
        style={styles.createBtn}
        onPress={() => navigation.navigate('CreateTicket')}
      >
        <Text style={styles.createBtnText}>+ Create New Ticket</Text>
      </TouchableOpacity>

      <FlatList
        data={mockTickets}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={styles.card}
            onPress={() => navigation.navigate('TicketDetail', { id: item.id })}
          >
            <View style={styles.header}>
              <Text style={styles.subject} numberOfLines={1}>{item.subject}</Text>
              <Text style={[styles.status, { color: getStatusColor(item.status) }]}>{item.status}</Text>
            </View>
            <Text style={styles.date}>{item.date}</Text>
          </TouchableOpacity>
        )}
      />
    </View>
  );
};

const getStatusColor = (status: string) => {
  switch (status) {
    case 'OPEN': return '#2196f3';
    case 'RESOLVED': return '#4caf50';
    case 'CLOSED': return '#757575';
    default: return '#ff9800';
  }
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5', padding: 16 },
  createBtn: { backgroundColor: '#2e7d32', padding: 15, borderRadius: 8, alignItems: 'center', marginBottom: 20 },
  createBtnText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  card: { backgroundColor: '#fff', padding: 16, borderRadius: 8, marginBottom: 12, elevation: 1 },
  header: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  subject: { fontSize: 16, fontWeight: 'bold', flex: 1, marginRight: 10 },
  status: { fontSize: 12, fontWeight: 'bold' },
  date: { fontSize: 12, color: '#999', marginTop: 8 }
});

export default SupportHomeScreen;
