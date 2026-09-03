import React from 'react';
import { View, Text, StyleSheet, ScrollView } from 'react-native';

const timeline = [
  { status: 'SUBMITTED', date: '2024-07-22 10:30 AM', remarks: 'Application successfully submitted on official portal.' },
  { status: 'IN_PROGRESS', date: '2024-07-21 02:00 PM', remarks: 'Processing started by employee Rahul.' },
  { status: 'PAYMENT_SUCCESSFUL', date: '2024-07-20 11:00 AM', remarks: 'Payment of ₹150 verified.' },
];

const ApplicationDetailScreen = () => {
  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>SSC CGL 2024</Text>
        <Text style={styles.appId}>Application ID: APP-2024-001</Text>
        <Text style={styles.status}>Current Status: SUBMITTED</Text>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Application Timeline</Text>
        {timeline.map((item, index) => (
          <View key={index} style={styles.timelineItem}>
            <View style={styles.timelineDot} />
            <View style={styles.timelineContent}>
              <Text style={styles.timelineStatus}>{item.status}</Text>
              <Text style={styles.timelineDate}>{item.date}</Text>
              <Text style={styles.timelineRemarks}>{item.remarks}</Text>
            </View>
          </View>
        ))}
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { padding: 20, backgroundColor: '#fff', marginBottom: 10 },
  title: { fontSize: 20, fontWeight: 'bold' },
  appId: { color: '#666', marginTop: 4 },
  status: { color: '#2e7d32', fontWeight: 'bold', marginTop: 8 },
  section: { padding: 20, backgroundColor: '#fff' },
  sectionTitle: { fontSize: 18, fontWeight: 'bold', marginBottom: 20 },
  timelineItem: { flexDirection: 'row', marginBottom: 20 },
  timelineDot: { width: 12, height: 12, borderRadius: 6, backgroundColor: '#2196f3', marginTop: 4, marginRight: 15 },
  timelineContent: { flex: 1, borderLeftWidth: 1, borderLeftColor: '#eee', paddingLeft: 15, marginLeft: -21 },
  timelineStatus: { fontWeight: 'bold', fontSize: 14 },
  timelineDate: { fontSize: 12, color: '#999', marginVertical: 2 },
  timelineRemarks: { fontSize: 13, color: '#666' }
});

export default ApplicationDetailScreen;
