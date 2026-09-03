import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity, Linking } from 'react-native';

const JobDetailScreen = ({ route, navigation }: any) => {
  const { jobId } = route.params;

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.org}>Staff Selection Commission (SSC)</Text>
        <Text style={styles.title}>SSC Combined Graduate Level Exam 2024</Text>
        <View style={styles.statusContainer}>
             <Text style={styles.statusText}>Potentially Eligible</Text>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Important Dates</Text>
        <Text>Application Start: 24/06/2024</Text>
        <Text style={styles.highlight}>Deadline: 15/09/2024</Text>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Fee Breakdown</Text>
        <View style={styles.row}>
          <Text>Official Application Fee</Text>
          <Text>₹100</Text>
        </View>
        <View style={styles.row}>
          <Text>Platform Service Fee</Text>
          <Text>₹50</Text>
        </View>
        <View style={[styles.row, styles.totalRow]}>
          <Text style={styles.totalText}>Total Amount</Text>
          <Text style={styles.totalText}>₹150</Text>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Eligibility Criteria</Text>
        <Text>• Bachelor's Degree in any stream</Text>
        <Text>• Age: 18 - 32 Years</Text>
      </View>

      <View style={styles.actions}>
        <TouchableOpacity style={styles.secondaryBtn} onPress={() => Linking.openURL('https://ssc.gov.in')}>
          <Text style={styles.secondaryBtnText}>View Official Notification</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.primaryBtn}
          onPress={() => navigation.navigate('ApplicationReadiness', { jobId })}
        >
          <Text style={styles.primaryBtnText}>Apply Through Our Service</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  header: { padding: 20, backgroundColor: '#f8f9fa', borderBottomWidth: 1, borderBottomColor: '#eee' },
  org: { color: '#666', fontSize: 14 },
  title: { fontSize: 22, fontWeight: 'bold', marginTop: 4, color: '#333' },
  statusContainer: { marginTop: 12, backgroundColor: '#fff3e0', padding: 8, borderRadius: 4, alignSelf: 'flex-start' },
  statusText: { color: '#ef6c00', fontWeight: 'bold', fontSize: 12 },
  section: { padding: 20, borderBottomWidth: 1, borderBottomColor: '#eee' },
  sectionTitle: { fontSize: 16, fontWeight: 'bold', marginBottom: 10, color: '#2c3e50' },
  highlight: { color: '#d32f2f', fontWeight: 'bold' },
  row: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 5 },
  totalRow: { marginTop: 10, paddingTop: 10, borderTopWidth: 1, borderTopColor: '#eee' },
  totalText: { fontWeight: 'bold', fontSize: 16 },
  actions: { padding: 20 },
  primaryBtn: { backgroundColor: '#2e7d32', padding: 16, borderRadius: 8, alignItems: 'center', marginTop: 10 },
  primaryBtnText: { color: '#fff', fontWeight: 'bold', fontSize: 16 },
  secondaryBtn: { borderWidth: 1, borderColor: '#1976d2', padding: 12, borderRadius: 8, alignItems: 'center' },
  secondaryBtnText: { color: '#1976d2', fontWeight: 'bold' }
});

export default JobDetailScreen;
