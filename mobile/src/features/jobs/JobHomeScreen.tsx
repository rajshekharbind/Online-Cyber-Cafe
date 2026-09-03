import React from 'react';
import { View, Text, StyleSheet, FlatList, TouchableOpacity, TextInput } from 'react-native';

const mockJobs = [
  { id: '1', title: 'SSC CGL 2024', organization: 'SSC', deadline: '2024-09-15', fee: '₹100', type: 'Government' },
  { id: '2', title: 'IBPS PO XIV', organization: 'IBPS', deadline: '2024-08-30', fee: '₹850', type: 'Government' },
  { id: '3', title: 'Railway Technician', organization: 'RRB', deadline: '2024-10-05', fee: '₹500', type: 'Government' },
];

const JobHomeScreen = ({ navigation }: any) => {
  return (
    <View style={styles.container}>
      <View style={styles.searchContainer}>
        <TextInput style={styles.searchInput} placeholder="Search jobs, organizations..." />
      </View>

      <FlatList
        data={mockJobs}
        keyExtractor={(item) => item.id}
        renderItem={({ item }) => (
          <TouchableOpacity
            style={styles.jobCard}
            onPress={() => navigation.navigate('JobDetail', { jobId: item.id })}
          >
            <View style={styles.jobHeader}>
              <Text style={styles.jobTitle}>{item.title}</Text>
              <View style={styles.badge}>
                <Text style={styles.badgeText}>{item.type}</Text>
              </View>
            </View>
            <Text style={styles.orgText}>{item.organization}</Text>
            <View style={styles.jobFooter}>
              <Text style={styles.deadline}>Deadline: {item.deadline}</Text>
              <Text style={styles.feeText}>Fee: {item.fee}</Text>
            </View>
          </TouchableOpacity>
        )}
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5', padding: 10 },
  searchContainer: { marginBottom: 15 },
  searchInput: { backgroundColor: '#fff', padding: 12, borderRadius: 8, borderWidth: 1, borderColor: '#ddd' },
  jobCard: { backgroundColor: '#fff', padding: 15, borderRadius: 10, marginBottom: 12, elevation: 2 },
  jobHeader: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'flex-start' },
  jobTitle: { fontSize: 18, fontWeight: 'bold', color: '#333', flex: 1 },
  badge: { backgroundColor: '#e3f2fd', paddingHorizontal: 8, paddingVertical: 4, borderRadius: 4 },
  badgeText: { fontSize: 10, color: '#1976d2', fontWeight: 'bold' },
  orgText: { color: '#666', marginTop: 4, fontSize: 14 },
  jobFooter: { flexDirection: 'row', justifyContent: 'space-between', marginTop: 12, borderTopWidth: 0.5, borderTopColor: '#eee', paddingTop: 8 },
  deadline: { fontSize: 12, color: '#d32f2f' },
  feeText: { fontSize: 12, fontWeight: 'bold' }
});

export default JobHomeScreen;
