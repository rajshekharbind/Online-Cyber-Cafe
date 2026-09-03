import React from 'react';
import { View, Text, StyleSheet, ScrollView, TouchableOpacity } from 'react-native';

const ProfileScreen = () => {
  const completionPercentage = 82; // This would come from Redux/API

  return (
    <ScrollView style={styles.container}>
      <View style={styles.header}>
        <Text style={styles.title}>Master Profile</Text>
        <View style={styles.progressContainer}>
            <View style={[styles.progressBar, { width: `${completionPercentage}%` }]} />
            <Text style={styles.progressText}>{completionPercentage}% Completed</Text>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Personal Details</Text>
        <DetailItem label="Full Name" value="John Doe" />
        <DetailItem label="Father's Name" value="Richard Doe" />
        <DetailItem label="Gender" value="Male" />
      </View>

      <View style={styles.section}>
        <Text style={styles.sectionTitle}>Education</Text>
        <DetailItem label="10th Grade" value="Passed (85%)" />
        <DetailItem label="12th Grade" value="Passed (82%)" />
      </View>

      <TouchableOpacity style={styles.editButton}>
        <Text style={styles.editButtonText}>Edit Profile</Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const DetailItem = ({ label, value }: { label: string; value: string }) => (
  <View style={styles.detailItem}>
    <Text style={styles.label}>{label}</Text>
    <Text style={styles.value}>{value}</Text>
  </View>
);

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#f5f5f5' },
  header: { padding: 20, backgroundColor: '#fff', marginBottom: 10 },
  title: { fontSize: 22, fontWeight: 'bold' },
  progressContainer: { marginTop: 10, height: 20, backgroundColor: '#e0e0e0', borderRadius: 10, overflow: 'hidden' },
  progressBar: { height: '100%', backgroundColor: '#4caf50' },
  progressText: { position: 'absolute', alignSelf: 'center', fontSize: 12, color: '#000' },
  section: { padding: 15, backgroundColor: '#fff', marginBottom: 10 },
  sectionTitle: { fontSize: 18, fontWeight: 'bold', marginBottom: 10, color: '#333' },
  detailItem: { flexDirection: 'row', justifyContent: 'space-between', paddingVertical: 8, borderBottomWidth: 0.5, borderBottomColor: '#eee' },
  label: { color: '#666' },
  value: { fontWeight: '500' },
  editButton: { margin: 20, backgroundColor: '#2196f3', padding: 15, borderRadius: 8, alignItems: 'center' },
  editButtonText: { color: '#fff', fontWeight: 'bold' }
});

export default ProfileScreen;
